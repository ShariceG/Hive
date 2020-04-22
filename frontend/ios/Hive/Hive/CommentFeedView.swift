//
//  CommentFeedView.swift
//  Hive
//
//  Created by Chuck Onwuzuruike on 9/20/19.
//  Copyright © 2019 Chuck Onwuzuruike. All rights reserved.
//

import Foundation
import UIKit

// Note: This entire file might as well be renamed to CommentFeedManager because
// that's basically what it is. The only difference is that this class is
// literally a View where as the PostFeedManager is not. Why did I do it this
// way instead of being consistent and following the manager pattern? I have no
// idea, I think i was experimenting on different ways of doing it and I was
// still learning.

protocol CommentFeedViewDelegate: class {
    func fetchComments(queryParams: QueryParams)
    func performAction(comment: Comment, actionType: ActionType)
}

class CommentFeedView : UIView, UITableViewDataSource, UITableViewDelegate, CommentViewDelegate {
    
    let COMMENT_VIEW_CELL_REUSE_IDENTIFIER = "commentView2"
    let COMMENT_VIEW_CELL_NIB_NAME = "CommentView"
 
    @IBOutlet weak var commentTableView: UITableView!
    public private(set) var comments: Array<Comment> = []
    public private(set) var delegate: CommentFeedViewDelegate?
    private(set) var prevFetchQueryMetadata: QueryMetadata = QueryMetadata()
    private var refreshControl: UIRefreshControl?
    
    func configure(delegate: CommentFeedViewDelegate) {
        self.delegate = delegate
        // Register nib as cell.
        // Overrides whatever identifier is used in storyboard... I think.
        commentTableView.register(UINib(nibName: COMMENT_VIEW_CELL_NIB_NAME, bundle: nil),
                               forCellReuseIdentifier: COMMENT_VIEW_CELL_REUSE_IDENTIFIER)
        commentTableView.delegate = self
        commentTableView.dataSource = self
        commentTableView.isScrollEnabled = true
        commentTableView.refreshControl = UIRefreshControl()
        refreshControl = commentTableView.refreshControl
        refreshControl!.addTarget(self, action: #selector(refreshPostTableView(_:)), for: .valueChanged)
        fetchMoreComments(getNewer: true)
    }
    
    // What to do when user forces a refresh on the table
    @objc func refreshPostTableView(_ refreshControl: UIRefreshControl) {
        fetchMoreComments(getNewer: true)
    }
    
    func reloadUI() {
        DispatchQueue.main.async {
            self.commentTableView.reloadData()
        }
        setRefreshing(set: false)
    }
    
    // Called by Controller to give us more hosts.
    func addMoreComments(moreComments: Array<Comment>, newMetadata: QueryMetadata) {
        self.prevFetchQueryMetadata.updateMetadata(newMetadata: newMetadata)
        var set: Set = Set(moreComments)
        
        // Merge old comments and new (moreComments) posts. If a post exists in old and in new, take the
        // new comment.
        for comment in comments {
            if !set.contains(comment) {
                set.insert(comment)
            }
        }
        comments = Array(set)
            .sorted(by: {$0.creationTimestampSec > $1.creationTimestampSec})
        reloadUI()
    }
    
    func setRefreshing(set: Bool) {
        DispatchQueue.main.async {
            if (self.refreshControl != nil) {
                if (set && !self.refreshControl!.isRefreshing) {
                    print("Start CommentFeed Refreshing...")
                    self.refreshControl?.beginRefreshing()
                } else {
                    print("Stop CommentFeed Refreshing...")
                    self.refreshControl?.endRefreshing()
                }
            }
        }
    }
    
    func reconfigureWithAction(commentId: String, actionType: ActionType) {
        let index = comments.firstIndex(where: {$0.commentId == commentId})
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
        let oldActionType = comments[i].userActionType
        if oldActionType == ActionType.LIKE {
            switch newActionType {
            case ActionType.LIKE:
                comments[i].likes -= 1;
                break;
            case ActionType.DISLIKE:
                comments[i].likes -= 1;
                comments[i].dislikes += 1;
                break;
            case ActionType.NO_ACTION:
                comments[i].likes -= 1;
                break;
            }
        }
        if oldActionType == ActionType.DISLIKE {
            switch newActionType {
            case ActionType.LIKE:
                comments[i].dislikes -= 1;
                comments[i].likes += 1;
                break;
            case ActionType.DISLIKE:
                comments[i].dislikes -= 1;
                break;
            case ActionType.NO_ACTION:
                comments[i].dislikes -= 1;
                break;
            }
        }
        if oldActionType == ActionType.NO_ACTION {
            switch newActionType {
            case ActionType.LIKE:
                comments[i].likes += 1;
                break;
            case ActionType.DISLIKE:
                comments[i].dislikes += 1;
                break;
            case ActionType.NO_ACTION:
                print("WARNING: This shouldn't happen but its not really an error.")
                break;
            }
        }
        comments[i].userActionType = newActionType
        reloadUI()
    }
    
    func fetchMoreComments(getNewer: Bool) {
        print("fetching " + (getNewer ? "newer" : "older") + " newer comments")
        if (!getNewer && !(prevFetchQueryMetadata.hasMoreOlderData ?? true)) {
            print("Server already told us there are no more new comments. Returning.")
            return
        }
        if (getNewer) {
            setRefreshing(set: true)
        }
        let params: QueryParams = QueryParams(getNewer: getNewer,
                                              currTopCursorStr: self.prevFetchQueryMetadata.newTopCursorStr,
                                              currBottomCursorStr: self.prevFetchQueryMetadata.newBottomCursorStr)
        delegate?.fetchComments(queryParams: params)
    }
    
    // A poke to the manager to ask for more new posts
    func pokeNew() {
        fetchMoreComments(getNewer: true)
    }
    
    // CommentViewDelegate overrides
    
    func performAction(commentView: CommentView, actionType: ActionType) {
        delegate!.performAction(comment: commentView.comment!, actionType: actionType)
    }

    // UITableViewDataSource protocol overrides
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return 1
    }

    // Asks for cell to display at indexPath
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: COMMENT_VIEW_CELL_REUSE_IDENTIFIER) as! CommentView
        cell.configure(comment: comments[indexPath.section], delegate: self)
        cell.layer.borderWidth = 2
        cell.layer.shadowColor = UIColor.black.cgColor
        cell.layer.shadowOpacity = 0.5
        cell.layer.shadowOffset = CGSize(width: 0.0, height: 2.0)
        cell.layer.borderColor = UIColor.white.cgColor
        return cell
    }
    
    // UITableViewDelegate overrides
    
    func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        return 15
    }
    
    func tableView(_ tableView: UITableView, viewForHeaderInSection section: Int) -> UIView?
    {
        let headerView = UIView(frame: CGRect(x: 0, y: 0, width: tableView.bounds.size.width, height: 5))
        headerView.backgroundColor = UIColor.clear
        
        return headerView
    }
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 120
    }
    
    func numberOfSections(in tableView: UITableView) -> Int {
        return self.comments.count
    }
    
    // Gives me cell from cellForRowAt right before it is displayed.
    func tableView(_ tableView: UITableView, willDisplay cell: UITableViewCell, forRowAt indexPath: IndexPath) {
        if indexPath.section == tableView.numberOfSections - 1 {
            if (!comments.isEmpty) {
                print("fetch older comments")
                fetchMoreComments(getNewer: false)
            }
        }
    }
}
