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
    
    let POST_VIEW_CELL_REUSE_IDENTIFIER = "postView"
    let POST_VIEW_CELL_NIB_NAME = "PostView"
    
    @IBOutlet weak var commentFeedView: CommentFeedView!
    @IBOutlet weak var postTableView: UITableView!
    @IBOutlet weak var commentBn: UIButton!
    @IBOutlet weak var commentTextView: UITextView!
    @IBOutlet weak var commentInputContainerView: UIView!
    @IBOutlet weak var commentsScrollView: UIScrollView!
    
    private(set) var post: Post? = nil
    private var disallowInteractingWithComments: Bool = false
    
    private let client: ServerClient = ServerClient()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.hideKeyboardWhenTapped()
        setupPostViewTable()
        setupExitGesture()
        setupCommentTv()
        self.postTableView.separatorColor = UIColor(red:0.97, green:0.82, blue:0.33, alpha:1.0)
        commentFeedView.configure(delegate: self)
        DispatchQueue.main.async {
            if self.disallowInteractingWithComments {
                self.commentBn.isHidden = true
                self.commentTextView.isHidden = true
            }
            self.postTableView.reloadData()
        }
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillDisappear), name: UIResponder.keyboardWillHideNotification, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillAppear), name: UIResponder.keyboardWillShowNotification, object: nil)
    }

    @objc func keyboardWillAppear(notification: NSNotification) {
        if let keyboardSize = (notification.userInfo?[UIResponder.keyboardFrameEndUserInfoKey] as? NSValue)?.cgRectValue {
            commentsScrollView.setContentOffset(CGPoint(x:
                0, y: keyboardSize.size.height), animated: true)
            commentFeedView.commentTableView.contentInset = UIEdgeInsets(top:
                keyboardSize.size.height, left: 0, bottom: 0, right: 0)
        }
    }

    @objc func keyboardWillDisappear(notification: NSNotification) {
        commentsScrollView.setContentOffset(
            CGPoint(x:0, y: 0), animated: true)
        commentFeedView.commentTableView.contentInset = UIEdgeInsets(top:
        0, left: 0, bottom: 0, right: 0)
    }

    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        NotificationCenter.default.removeObserver(self)
    }
    
    public func controllerInit(post: Post) {
        self.post = post
    }
    
    public func controllerInit(post: Post, disallowInteractingWithComments: Bool) {
        self.post = post
        self.disallowInteractingWithComments = disallowInteractingWithComments
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
    
    private func setupPostViewTable() {
        postTableView.delegate = self
        postTableView.dataSource = self
        postTableView.isScrollEnabled = true
        postTableView.register(UINib(nibName: POST_VIEW_CELL_NIB_NAME, bundle: nil), forCellReuseIdentifier: POST_VIEW_CELL_REUSE_IDENTIFIER)
        postTableView.isScrollEnabled = false
    }

    @objc func exitViewController(recognizer: UISwipeGestureRecognizer) {
        _ = self.navigationController?.popViewController(animated: true)
        self.dismiss(animated: true, completion: nil)
    }
    
    public func getAllPostComments(queryParams: QueryParams) {
        self.client.getAllCommentsForPost(username: self.getLoggedInUsername(),
                                          postId: (self.post?.postId)!, queryParams: queryParams,
                                          completion: self.getAllPostCommentsCompletion, notes: nil)
    }
    
    private func getAllPostCommentsCompletion(responseOr: StatusOr<Response>, notes: [String:Any]?) {
        if responseOr.hasError() || !responseOr.get().ok() {
            if (!responseOr.hasError()) {
                print("ERROR_FROM_SERVER: " + responseOr.get().getServerErrorStr());
            } else {
                print("ERROR CONNECTION: " + responseOr.getErrorMessage());
            }
            commentFeedView.reloadUI();
            showInternalServerErrorAlert();
            return;
        }
        let response = responseOr.get()
        let comments = response.comments
        let newMetadata = response.queryMetadata
        commentFeedView.addMoreComments(moreComments: comments, newMetadata: newMetadata)
        DispatchQueue.main.async {
            self.commentFeedView.reloadUI()
        }
    }
    
    @IBAction func commentBnAction(_ sender: UIButton) {
        if (commentTextView.text.isEmpty) {
            return
        }
        DispatchQueue.main.async {
            self.commentBn.isEnabled = false
            self.commentTextView.isEditable = false
            self.commentTextView.isSelectable = false
            sender.isEnabled = false
        }
        commentFeedView.setRefreshing(set: true)
        client.insertComment(username: self.getLoggedInUsername(), commentText: commentTextView.text,
                             postId: (post?.postId)!, completion: insertCommentCompletion, notes: nil)
    }
    
    private func insertCommentCompletion(responseOr: StatusOr<Response>, notes: [String:Any]?) {
        if responseOr.hasError() || !responseOr.get().ok() {
            if (!responseOr.hasError()) {
                print("ERROR_FROM_SERVER: " + responseOr.get().getServerErrorStr());
            } else {
                print("ERROR CONNECTION: " + responseOr.getErrorMessage());
            }
            commentFeedView.reloadUI();
            showInternalServerErrorAlert();
            return;
        }
        self.commentFeedView.addMoreComments(moreComments: responseOr.get().comments,
                                             newMetadata: QueryMetadata())
        DispatchQueue.main.async {
            self.commentTextView.isEditable = true
            self.commentTextView.isSelectable = true
            self.commentBn.isEnabled = true
            self.commentTextView.text = ""
        }
    }
    
    private func updateComment(comment: Comment, actionType: ActionType) {
        client.updateComment(commentId: comment.commentId, username: getLoggedInUsername(),
            actionType: actionType, completion: updateCommentCompletion,
            notes: ["actionType": actionType, "commentId": comment.commentId])
    }
    
    private func updateCommentCompletion(responseOr: StatusOr<Response>, notes: [String:Any]?) {
        if responseOr.hasError() || !responseOr.get().ok() {
            if (!responseOr.hasError()) {
                print("ERROR_FROM_SERVER: " + responseOr.get().getServerErrorStr());
            } else {
                print("ERROR CONNECTION: " + responseOr.getErrorMessage());
            }
            commentFeedView.reloadUI();
            showInternalServerErrorAlert();
            return;
        }
        let actionType = notes!["actionType"] as! ActionType
        let commentId = notes!["commentId"] as! String
        DispatchQueue.main.async {
            self.commentFeedView.reconfigureWithAction(commentId: commentId, actionType: actionType)
        }
    }
}

// --------------- Extensions ----------------

extension CommentsViewController: UITableViewDataSource, UITableViewDelegate {
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return 1
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell: PostView = tableView.dequeueReusableCell(withIdentifier: POST_VIEW_CELL_REUSE_IDENTIFIER) as! PostView
        cell.configureDisable(post: self.post!)
        cell.layer.borderWidth = 2
        cell.layer.cornerRadius = 5
        cell.layer.borderColor = UIColor.blue.cgColor
        DispatchQueue.main.async {
            cell.commentBn.isEnabled = false
        }
        return cell
    }
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 120
    }
    
    func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
}

extension CommentsViewController: CommentFeedViewDelegate {
    func fetchComments(queryParams: QueryParams) {
        self.getAllPostComments(queryParams: queryParams)
    }
    
    func performAction(comment: Comment, actionType: ActionType) {
        if self.disallowInteractingWithComments {
            return
        }
        self.updateComment(comment: comment, actionType: actionType)
    }
}
