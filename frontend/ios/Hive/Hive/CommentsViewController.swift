//
//  CommentsViewController.swift
//  Hive
//
//  Created by Chuck Onwuzuruike on 10/7/18.
//  Copyright © 2018 Chuck Onwuzuruike. All rights reserved.
//

import Foundation
import UIKit

class CommentsViewController: UIViewController {
    
    @IBOutlet weak var commentTableView: UITableView!
    @IBOutlet weak var postTableView: UITableView!
    @IBOutlet weak var commentBn: UIButton!
    @IBOutlet weak var commentTextView: UITextView!
    
    private(set) var post: Post? = nil
    
    private let client: ServerClient = ServerClient()
    private(set) var fetchCommentsMetadata: QueryMetadata = QueryMetadata()
    private(set) var allPostComments: Array<Comment> = []
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.hideKeyboardWhenTapped()
        setupTables()
        setupExitGesture()
        setupCommentTv()
        DispatchQueue.main.async {
            self.postTableView.reloadData()
        }
        getAllPostComments(getNewComments: true)
    }
    
    public func controllerInit(post: Post) {
        self.post = post
    }
    
    private func setupCommentTv() {
        commentTextView.layer.cornerRadius = 10.0
        commentTextView.layer.borderWidth = 1.0
        commentTextView.layer.borderColor = UIColor.black.cgColor
    }
    
    private func setupExitGesture() {
        let gesture = UISwipeGestureRecognizer(target: self, action: #selector(exitViewController))
        gesture.numberOfTouchesRequired = 1
        gesture.direction = .down
        postTableView.addGestureRecognizer(gesture)
    }
    
    private func setupTables() {
        postTableView.delegate = self
        postTableView.dataSource = self
        postTableView.isScrollEnabled = true
        postTableView.register(UINib(nibName: "PostView", bundle: nil), forCellReuseIdentifier: "postViewCell")
        postTableView.isScrollEnabled = false

        commentTableView.delegate = self
        commentTableView.dataSource = self
        commentTableView.isScrollEnabled = true
        commentTableView.register(UINib(nibName: "CommentView", bundle: nil), forCellReuseIdentifier: "commentViewCell")
        // Take care of refreshing
        commentTableView.refreshControl = UIRefreshControl()
        commentTableView.refreshControl!.addTarget(self, action: #selector(refreshCommentTableView(_:)), for: .valueChanged)
    }

    @objc func exitViewController(recognizer: UISwipeGestureRecognizer) {
        _ = self.navigationController?.popViewController(animated: true)
        self.dismiss(animated: true, completion: nil)
    }

    @objc func refreshCommentTableView(_ refreshControl: UIRefreshControl) {
        // This should also end the refresh.
        getAllPostComments(getNewComments: true)
    }
    
    public func getAllPostComments(getNewComments: Bool) {
        if (!getNewComments && !(fetchCommentsMetadata.hasMoreOlderData ?? true)) {
            return
        }
        let params: QueryParams = QueryParams(getNewer: getNewComments, currTopCursorStr: self.fetchCommentsMetadata.newTopCursorStr, currBottomCursorStr: self.fetchCommentsMetadata.newBottomCursorStr)
        client.getAllCommentsForPost(postId: (post?.postId)!, queryParams: params, completion: getAllPostCommentsCompletion)
    }
    
    private func getAllPostCommentsCompletion(responseOr: StatusOr<Response>) {
        if (responseOr.hasError()) {
            // Handle likley connection error
            print("Connection Failure: " + responseOr.getErrorMessage())
            return
        }
        if (!responseOr.get().ok()) {
            // Handle server error
            print("ServerStatusCode: " + String(describing: responseOr.get().serverStatusCode))
            return
        }

        fetchCommentsMetadata.updateMetadata(newMetadata: responseOr.get().queryMetadata)
        allPostComments.append(contentsOf: responseOr.get().comments)
        allPostComments = allPostComments.sorted(by: {$0.creationTimestampSec > $1.creationTimestampSec})
        print("Got post comments: ", responseOr.get().comments)
    
        DispatchQueue.main.async {
            self.commentTableView.reloadData()
            if (self.commentTableView.refreshControl != nil) {
                self.commentTableView.refreshControl?.endRefreshing()
            }
        }
    }
    
    @IBAction func commentBnAction(_ sender: UIButton) {
        if (commentTextView.text.isEmpty) {
            return
        }
        DispatchQueue.main.async {
            self.commentTextView.isEditable = false
            self.commentTextView.isSelectable = false
            sender.isEnabled = false
        }
        client.insertComment(username: self.getTestUser(), commentText: commentTextView.text, postId: (post?.postId)!, completion: insertCommentCompletion)
    }
    
    private func insertCommentCompletion(response: StatusOr<Response>) {
        var error: Bool = false
        if (response.hasError()) {
            // Handle likley connection error
            print("Connection Failure: " + response.getErrorMessage())
            error = true
        }
        if (response.get().serverStatusCode != ServerStatusCode.OK) {
            // Handle server error
            print("ServerStatusCode: " + String(describing: response.get().serverStatusCode))
            error = true
        }
        DispatchQueue.main.async {
            self.commentTextView.isEditable = true
            self.commentTextView.isSelectable = true
            if (!error) {
                self.commentTextView.text = ""
            }
            self.commentBn.isEnabled = true
        }
    }
}

// --------------- Extensions ----------------

extension CommentsViewController: UITableViewDataSource, UITableViewDelegate {
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return 1
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if (tableView == postTableView && self.post != nil) {
            let cell: PostView = tableView.dequeueReusableCell(withIdentifier: "postViewCell") as! PostView
            cell.configure(post: self.post!)
            cell.layer.borderWidth = 2
            cell.layer.cornerRadius = 5
            cell.layer.borderColor = UIColor.blue.cgColor
            DispatchQueue.main.async {
                cell.commentBn.isEnabled = false
            }
            return cell
        } else {
            let cell: CommentView = tableView.dequeueReusableCell(withIdentifier: "commentViewCell") as! CommentView
            cell.configure(comment: allPostComments[indexPath.section])
            cell.layer.borderWidth = 2
            cell.layer.cornerRadius = 5
            cell.layer.borderColor = UIColor.blue.cgColor
            return cell
        }
    }
    
    func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        return 0
    }
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return tableView == postTableView ? tableView.layer.bounds.height : 120
    }
    
    func numberOfSections(in tableView: UITableView) -> Int {
        return tableView == postTableView ? 1 : self.allPostComments.count
    }
    
    func tableView(_ tableView: UITableView, willDisplay cell: UITableViewCell, forRowAt indexPath: IndexPath) {
        if indexPath.section == tableView.numberOfSections - 1 &&
            indexPath.row == tableView.numberOfRows(inSection: indexPath.section) - 1 {
            if (!allPostComments.isEmpty) {
                self.getAllPostComments(getNewComments: false)
            }
        }
    }
}
