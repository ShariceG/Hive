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
    func showComments(postView: PostView);
    func fetchMorePosts(queryParams: QueryParams);
    func performAction(post: Post, actionType: ActionType);
}

class PostFeedManager: NSObject, PostViewDelegate {
    
    let POST_VIEW_CELL_REUSE_IDENTIFIER = "postView"
    let POST_VIEW_CELL_NIB_NAME = "PostView"
    
    private weak var _postTableView: UITableView?
    public private(set) var posts: Array<Post> = []
    public private(set) var delegate: PostFeedDelegate?
    private var prevFetchQueryMetadata: QueryMetadata = QueryMetadata()
    
    var postTableView: UITableView {
        get { return _postTableView! }
    }

    func configure(tableView: UITableView, delegate: PostFeedDelegate) {
        self._postTableView = tableView
        self.delegate = delegate
        // Register nib as cell.
        postTableView.register(UINib(nibName: POST_VIEW_CELL_NIB_NAME, bundle: nil),
                               forCellReuseIdentifier: POST_VIEW_CELL_REUSE_IDENTIFIER)
        postTableView.delegate = self
        postTableView.dataSource = self
        postTableView.isScrollEnabled = true
        postTableView.refreshControl = UIRefreshControl()
        postTableView.refreshControl!.addTarget(self, action: #selector(refreshPostTableView(_:)), for: .valueChanged)
        
        // Fetch an initial set of hosts.
        fetchMorePosts(getNewer: true)
    }
    
    // What to do when user forces a refresh on the table
    @objc func refreshPostTableView(_ refreshControl: UIRefreshControl) {
        print("fetch newer posts")
        fetchMorePosts(getNewer: true)
        endPostTableViewRefresh()
    }
    
    func reload() {
        postTableView.reloadData()
    }
    
    func endPostTableViewRefresh() {
        if (self.postTableView.refreshControl != nil) {
            self.postTableView.refreshControl?.endRefreshing()
        }
    }
    
    // Called by Controller to give us more hosts.
    func addMorePosts(morePosts: Array<Post>, newMetadata: QueryMetadata) {
        self.prevFetchQueryMetadata.updateMetadata(newMetadata: newMetadata)
        posts.append(contentsOf: morePosts)
        posts = Array(Set(posts))
        posts = posts.sorted(by: {$0.creationTimestampSec > $1.creationTimestampSec})
        posts = posts.filter {!$0.isExpired()}
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
        let thePost = posts.filter {$0.postId == postId}
        // filter will return a list but it should be one item long, unless
        // the post doesn't exist anymore.
        if thePost.count > 0 {
            posts[0].userActionType = actionType
            reload()
        }
    }
    
    private func fetchMorePosts(getNewer: Bool) {
        if (!getNewer && !(prevFetchQueryMetadata.hasMoreOlderData ?? true)) {
            print("Server already told us there are no more new posts. Returning.")
            return
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
        print("Attempting to show: " + indexPath.description)
        cell.configure(post: posts[indexPath.section], delegate: self)
        cell.layer.borderWidth = 2
        cell.layer.cornerRadius = 5
        cell.layer.borderColor = UIColor.blue.cgColor
        return cell
    }
}

extension PostFeedManager: UITableViewDataSource {
    func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        return 10
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
