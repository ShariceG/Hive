//
//  ViewController.swift
//  Hive
//
//  Created by Chuck Onwuzuruike on 10/3/18.
//  Copyright © 2018 Chuck Onwuzuruike. All rights reserved.
//

import UIKit

class ViewController: UIViewController {
    
    let COMMENTS_SEGUE_IDENTIFIER = "seeCommentsSegue"
    
    @IBOutlet weak var postFeedTable: UITableView!
    @IBOutlet weak var writePostButtonItem: UIBarButtonItem!
    private var makePostView: MakePostView!;
    
    private var postFeedManager = PostFeedManager()
    private(set) var client: ServerClient = ServerClient()

    override func viewDidLoad() {
        super.viewDidLoad()
        self.hideKeyboardWhenTapped()
        setupMakePostAlert()
        setupPostFeedView()
        writePostButtonItem.action = #selector(showMakePostView)
    }
    
    @objc func showMakePostView(){
        makePostView.show()
    }
    
    private func setupPostFeedView() {
        postFeedManager.configure(tableView: postFeedTable, delegate: self)
    }
    
    private func setupMakePostAlert() {
        makePostView = UIView.loadFromNibNamed(nibNamed: "MakePostView") as? MakePostView
        makePostView.configure(parent: self.view, delegate: self)
    }
    
    private func insertPostCompletion(responseOr: StatusOr<Response>, notes: [String:Any]?) {
        if responseOr.hasError() || !responseOr.get().ok() {
            if (!responseOr.hasError()) {
                print("ERROR_FROM_SERVER: " + responseOr.get().getServerErrorStr());
            } else {
                print("ERROR CONNECTION: " + responseOr.getErrorMessage());
            }
            postFeedManager.reloadUI();
            showInternalServerErrorAlert();
            return;
        }
        self.postFeedManager.addMorePosts(morePosts: responseOr.get().posts,
                                             newMetadata: QueryMetadata())
        makePostView.clearPostText()
        DispatchQueue.main.async {
            self.view.isUserInteractionEnabled = true
            self.postFeedManager.reloadUI()
        }
    }
    
    private func updatePostCompletion(responseOr: StatusOr<Response>, notes: [String:Any]?) {
        if responseOr.hasError() || !responseOr.get().ok() {
            if (!responseOr.hasError()) {
                print("ERROR_FROM_SERVER: " + responseOr.get().getServerErrorStr());
            } else {
                print("ERROR CONNECTION: " + responseOr.getErrorMessage());
            }
            postFeedManager.reloadUI();
            showInternalServerErrorAlert();
            return;
        }
        let actionType = notes!["actionType"] as! ActionType
        let postId = notes!["postId"] as! String
        DispatchQueue.main.async {
            self.postFeedManager.reconfigureWithAction(postId: postId, actionType: actionType)
        }
    }
    
    private func fetchPostsAroundUserCompletion(responseOr: StatusOr<Response>, notes: [String:Any]?) {
        if responseOr.hasError() || !responseOr.get().ok() {
            if (!responseOr.hasError()) {
                print("ERROR_FROM_SERVER: " + responseOr.get().getServerErrorStr());
            } else {
                print("ERROR CONNECTION: " + responseOr.getErrorMessage());
            }
            postFeedManager.reloadUI();
            showInternalServerErrorAlert();
            return;
        }
        
        let response = responseOr.get()
        let newPosts = response.posts
        let newMetdata = response.queryMetadata
    
        self.postFeedManager.addMorePosts(morePosts: newPosts, newMetadata: newMetdata)
        DispatchQueue.main.async {
            self.postFeedManager.reloadUI()
        }
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if (segue.identifier == COMMENTS_SEGUE_IDENTIFIER) {
            let postView: PostView = sender as! PostView
            let commentsViewController: CommentsViewController = segue.destination as! CommentsViewController
            commentsViewController.controllerInit(post: postView.post)
            return
        }
    }
}

// Delegate functions
extension ViewController: PostFeedDelegate {
    func showComments(postView: PostView) {
        self.performSegue(withIdentifier: COMMENTS_SEGUE_IDENTIFIER, sender: postView)
    }
    
    func fetchMorePosts(queryParams: QueryParams) {
        client.getAllPostsAtLocation(username: getLoggedInUsername(),
                                     location: self.getCurrentUserLocation(),
                                     queryParams: queryParams,
                                     completion:fetchPostsAroundUserCompletion, notes: nil)
    }

    func performAction(post: Post, actionType: ActionType) {
        client.updatePost(postId: post.postId, username: getLoggedInUsername(),
            actionType: actionType, completion: updatePostCompletion,
            notes: ["actionType": actionType, "postId": post.postId])
    }
}

extension ViewController: MakePostViewDelegate {
    func makePost(text: String) {
        makePostView.hide()
        DispatchQueue.main.async {
            self.view.isUserInteractionEnabled = true
        }
        postFeedManager.setRefreshing(set: true)
        client.insertPost(username: self.getLoggedInUsername(),
                          postText: text, location: self.getCurrentUserLocation(),
                          completion: insertPostCompletion, notes: nil)
    }
}
