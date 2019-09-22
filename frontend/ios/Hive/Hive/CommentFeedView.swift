//
//  CommentFeedView.swift
//  Hive
//
//  Created by Chuck Onwuzuruike on 9/20/19.
//  Copyright © 2019 Chuck Onwuzuruike. All rights reserved.
//

import Foundation
import UIKit

protocol CommentFeedViewDelegate: class {
    func fetchComments(queryParams: QueryParams)
}

class CommentFeedView : UIView, UITableViewDataSource, UITableViewDelegate, CommentViewDelegate {
    
    let COMMENT_VIEW_CELL_REUSE_IDENTIFIER = "commentView"
    let COMMENT_VIEW_CELL_NIB_NAME = "CommentView"
 
    @IBOutlet weak var commentTableView: UITableView!
    public private(set) var comments: Array<Comment> = []
    public private(set) var delegate: CommentFeedViewDelegate?
    private(set) var prevFetchQueryMetadata: QueryMetadata = QueryMetadata()
    
    func configure(delegate: CommentFeedViewDelegate) {
        self.delegate = delegate
        // Register nib as cell.
        commentTableView.register(UINib(nibName: COMMENT_VIEW_CELL_NIB_NAME, bundle: nil),
                               forCellReuseIdentifier: COMMENT_VIEW_CELL_REUSE_IDENTIFIER)
        commentTableView.delegate = self
        commentTableView.dataSource = self
        commentTableView.isScrollEnabled = true
        commentTableView.refreshControl = UIRefreshControl()
        commentTableView.refreshControl!.addTarget(self, action: #selector(refreshPostTableView(_:)), for: .valueChanged)
        fetchMoreComments(getNewer: true)
    }
    
    // What to do when user forces a refresh on the table
    @objc func refreshPostTableView(_ refreshControl: UIRefreshControl) {
        print("fetch newer comments")
        fetchMoreComments(getNewer: true)
        endPostTableViewRefresh()
    }
    
    func reload() {
        commentTableView.reloadData()
    }
    
    func endPostTableViewRefresh() {
        if (self.commentTableView.refreshControl != nil) {
            self.commentTableView.refreshControl?.endRefreshing()
        }
    }
    
    // Called by Controller to give us more hosts.
    func addMoreComments(moreComments: Array<Comment>, newMetadata: QueryMetadata) {
        self.prevFetchQueryMetadata.updateMetadata(newMetadata: newMetadata)
        comments.append(contentsOf: moreComments)
        comments = comments.sorted(by: {$0.creationTimestampSec > $1.creationTimestampSec})
    }
    
    func fetchMoreComments(getNewer: Bool) {
        if (!getNewer && !(prevFetchQueryMetadata.hasMoreOlderData ?? true)) {
            print("Server already told us there are no more new comments. Returning.")
            return
        }
        let params: QueryParams = QueryParams(getNewer: getNewer,
                                              currTopCursorStr: self.prevFetchQueryMetadata.newTopCursorStr,
                                              currBottomCursorStr: self.prevFetchQueryMetadata.newBottomCursorStr)
        delegate?.fetchComments(queryParams: params)
    }

    // UITableViewDataSource protocol overrides
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return 1
    }

    // Asks for cell to display at indexPath
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: COMMENT_VIEW_CELL_REUSE_IDENTIFIER) as! CommentView
        print("Attempting to show: " + indexPath.description)
        cell.configure(comment: comments[indexPath.section], delegate: self)
        cell.layer.borderWidth = 2
        cell.layer.cornerRadius = 5
        cell.layer.borderColor = UIColor.blue.cgColor
        return cell
    }
    
    // UITableViewDelegate overrides
    
    func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        return 10
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
