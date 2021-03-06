//
//  PostFeedManager.swift
//  Hive
//
//  Created by Chuck Onwuzuruike on 9/22/19.
//  Copyright © 2019 Chuck Onwuzuruike. All rights reserved.
//

import Foundation
import UIKit

protocol PostFeedDelegate: class {
    func showComments(postView: PostView)
    func fetchMorePosts(queryParams: QueryParams)
    func performAction(post: Post, actionType: ActionType)
}

class PostFeedManager: NSObject, PostViewDelegate {
    
    let POST_VIEW_CELL_REUSE_IDENTIFIER = "postView"
    let POST_VIEW_CELL_NIB_NAME = "PostView"
    
    private weak var _postTableView: UITableView?
    public private(set) var posts: Array<Post> = []
    public private(set) var delegate: PostFeedDelegate?
    private var prevFetchQueryMetadata: QueryMetadata = QueryMetadata()
    private var refreshControl: UIRefreshControl?
    
    var postTableView: UITableView {
        get { return _postTableView! }
    }

    func configure(tableView: UITableView, delegate: PostFeedDelegate) {
        self._postTableView = tableView
        self.delegate = delegate
        // Register nib as cell.
        // Overrides whatever identifier is used in storyboard... I think.
        postTableView.register(UINib(nibName: POST_VIEW_CELL_NIB_NAME, bundle: nil),
                               forCellReuseIdentifier: POST_VIEW_CELL_REUSE_IDENTIFIER)
        postTableView.delegate = self
        postTableView.dataSource = self
        postTableView.isScrollEnabled = true
        postTableView.refreshControl = UIRefreshControl()
        refreshControl = postTableView.refreshControl
        refreshControl!.addTarget(self, action: #selector(refreshPostTableView(_:)), for: .valueChanged)
        refreshControl!.attributedTitle = NSAttributedString(string: "ugh, hold on...")
        
        // Fetch an initial set of hosts.
        fetchMorePosts(getNewer: true)
    }
    
    // What to do when user forces a refresh on the table
    @objc func refreshPostTableView(_ refreshControl: UIRefreshControl) {
        print("fetch newer posts")
        fetchMorePosts(getNewer: true)
    }
    
    func reloadUI() {
        DispatchQueue.main.async {
            self.postTableView.reloadData()
        }
        setRefreshing(set: false)
    }
    
    func setRefreshing(set: Bool) {
        DispatchQueue.main.async {
            if (self.refreshControl != nil) {
                if (set && !self.refreshControl!.isRefreshing) {
                    print("Start PostFeed Refreshing...")
                    self.refreshControl?.beginRefreshing()
                } else {
                    print("Stop PostFeed Refreshing...")
                    self.refreshControl?.endRefreshing()
                }
            }
        }
    }
    
    // Called by Controller to give us more hosts.
    func addMorePosts(morePosts: Array<Post>, newMetadata: QueryMetadata) {
        self.prevFetchQueryMetadata.updateMetadata(newMetadata: newMetadata)
        var set: Set = Set(morePosts)
        
        // Merge old posts and new (morePosts) posts. If a post exists in old and in new, take the
        // new post.
        for post in posts {
            if !set.contains(post) {
                set.insert(post)
            }
        }
        posts = Array(set)
            .filter {!$0.isExpired()}
            .sorted(by: {$0.creationTimestampSec > $1.creationTimestampSec})
    }
    
    func resetData() {
        self.posts = []
        self.prevFetchQueryMetadata = QueryMetadata()
    }
    
    func reset(newTableView: UITableView) {
        self._postTableView = newTableView
        resetData()
    }
    
    // A poke to the manager to ask for more new posts
    func pokeNew() {
        fetchMorePosts(getNewer: true)
    }
    
    // Will reset data and then request new data
    func resetDataAndPokeNew() {
        resetData()
        pokeNew()
    }
    
    func reconfigureWithAction(postId: String, actionType: ActionType) {
        let index = posts.firstIndex(where: {$0.postId == postId})
        if index == nil {
            return;
        }
        let i = index!
        // filter will return a list but it should be one item long, unless
        // the post doesn't exist anymore or some weird duplicate error, which
        // shouldn't happen since we remove duplicates.

        // Now change the # of likes and dislikes by 1 depending on the old and
        // new action type.
        let newActionType = actionType
        let oldActionType = posts[i].userActionType
        if oldActionType == ActionType.LIKE {
            switch newActionType {
            case ActionType.LIKE:
                posts[i].likes -= 1;
                break;
            case ActionType.DISLIKE:
                posts[i].likes -= 1;
                posts[i].dislikes += 1;
                break;
            case ActionType.NO_ACTION:
                posts[i].likes -= 1;
                break;
            }
        }
        if oldActionType == ActionType.DISLIKE {
            switch newActionType {
            case ActionType.LIKE:
                posts[i].dislikes -= 1;
                posts[i].likes += 1;
                break;
            case ActionType.DISLIKE:
                posts[i].dislikes -= 1;
                break;
            case ActionType.NO_ACTION:
                posts[i].dislikes -= 1;
                break;
            }
        }
        if oldActionType == ActionType.NO_ACTION {
            switch newActionType {
            case ActionType.LIKE:
                posts[i].likes += 1;
                break;
            case ActionType.DISLIKE:
                posts[i].dislikes += 1;
                break;
            case ActionType.NO_ACTION:
                print("WARNING: This shouldn't happen but its not really an error.")
                break;
            }
        }
        posts[i].userActionType = newActionType
        reloadUI()
    }
    
    private func fetchMorePosts(getNewer: Bool) {
        if (!getNewer && !(prevFetchQueryMetadata.hasMoreOlderData ?? true)) {
            print("Server already told us there are no more old posts. Returning.")
            return
        }
        if (getNewer) {
            setRefreshing(set: true)
        }
        let params: QueryParams = QueryParams(getNewer: getNewer,
                                              currTopCursorStr: self.prevFetchQueryMetadata.newTopCursorStr,
                                              currBottomCursorStr: self.prevFetchQueryMetadata.newBottomCursorStr)
        delegate?.fetchMorePosts(queryParams: params)
    }
    
    func commentButtonClick(postView: PostView) {
        delegate?.showComments(postView: postView);
    }
    
    func performAction(postView: PostView, actionType: ActionType) {
        delegate?.performAction(post: postView.post, actionType: actionType)
    }
}

extension PostFeedManager: UITableViewDelegate {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return 1
    }

    // Asks for cell to display at indexPath
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: POST_VIEW_CELL_REUSE_IDENTIFIER) as! PostView
        cell.configure(post: posts[indexPath.section], delegate: self)
        cell.layer.borderWidth = 2
        cell.layer.shadowColor = UIColor.black.cgColor
        cell.layer.shadowOpacity = 0.5
        cell.layer.shadowOffset = CGSize(width: 0.0, height: 2.0)
        cell.layer.borderColor = UIColor.white.cgColor
        return cell
    }
}

extension PostFeedManager: UITableViewDataSource {
    func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        return 15
    }
    
    func tableView(_ tableView: UITableView, viewForHeaderInSection section: Int) -> UIView?
    {
        let headerView = UIView(frame: CGRect(x: 0, y: 0, width: tableView.bounds.size.width, height: 10))
        headerView.backgroundColor = UIColor.clear
        
        return headerView
    }
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 120
    }
    
    func numberOfSections(in tableView: UITableView) -> Int {
        return self.posts.count
    }
    
    // Gives me cell from cellForRowAt right before it is displayed.
    func tableView(_ tableView: UITableView, willDisplay cell: UITableViewCell, forRowAt indexPath: IndexPath) {
        if indexPath.section == tableView.numberOfSections - 1 {
            if (!posts.isEmpty) {
                print("fetch older posts")
                fetchMorePosts(getNewer: false)
            }
        }
    }
}
