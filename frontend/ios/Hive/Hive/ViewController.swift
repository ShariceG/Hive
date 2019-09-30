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
    @IBOutlet weak var postTv: UITextView!
    @IBOutlet weak var postBn: UIButton!
    
    private var postFeedManager = PostFeedManager()
    private(set) var client: ServerClient = ServerClient()
    private(set) var fetchPostsMetadata: QueryMetadata = QueryMetadata()

    override func viewDidLoad() {
        super.viewDidLoad()
        self.hideKeyboardWhenTapped()
        setupPostTv()
        setupPostBn()
        setupPostFeedView()
    }

    private func setupPostBn() {
        postBn.layer.cornerRadius = 10
        postBn.layer.borderWidth = 1
        postBn.layer.borderColor = UIColor.black.cgColor
    }
    
    private func setupPostTv() {
        // Take care of border
        postTv.layer.cornerRadius = 10.0
        postTv.layer.borderWidth = 1.0
        postTv.layer.borderColor = UIColor.black.cgColor
    }
    
    private func setupPostFeedView() {
        postFeedManager.configure(tableView: postFeedTable, delegate: self)
    }

    @IBAction func postBnAction(_ sender: UIButton) {
        if (postTv.text.isEmpty) {
            return
        }
        DispatchQueue.main.async {
            self.postTv.isEditable = false
            self.postTv.isSelectable = false
            sender.isEnabled = false
        }
        client.insertPost(username: self.getTestUser(),
                          postText: postTv.text, location: self.getTestLocation(),
                          completion: insertPostCompletion)
    }
    
    private func insertPostCompletion(response: StatusOr<Response>) {
        var error: Bool = false
        let baseStr: String = "insertPostCompletion => "
        if (response.hasError()) {
            // Handle likley connection error
            print(baseStr + "Connection Failure: " + response.getErrorMessage())
            error = true
        }
        if (!error && response.get().serverStatusCode != ServerStatusCode.OK) {
            // Handle server error
            print(baseStr + "ServerStatusCode: " + String(describing: response.get().serverStatusCode))
            error = true
        }
        
        if (!error) {
            print(baseStr + "Inserted post successfully!")
        }
        
        self.postFeedManager.pokeNew()
        DispatchQueue.main.async {
            self.postTv.isEditable = true
            self.postTv.isSelectable = true
            if (!error) {
                self.postTv.text = ""
            }
            self.postBn.isEnabled = true
            self.postFeedManager.reload()
        }
    }
    
//    private func getAllPostsByUserCompletion(response: StatusOr<Response>) {
//        if (response.hasError()) {
//            // Handle likley connection error
//            print("Connection Failure: " + response.getErrorMessage())
//            return
//        }
//        if (response.get().serverStatusCode != ServerStatusCode.OK) {
//            // Handle server error
//            print("ServerStatusCode: " + String(describing: response.get().serverStatusCode))
//            return
//        }
//        allPostsByUser.append(contentsOf: response.get().posts)
//        print("Got posts..." + String(allPostsByUser.count))
//        
//        DispatchQueue.main.async {
//            self.postTableView.reloadData()
//        }
//    }
    
    private func fetchPostsAroundUserCompletion(responseOr: StatusOr<Response>) {
        let baseStr: String = "fetchPostsAroundUserCompletion => "
        if (responseOr.hasError()) {
            // Handle likley connection error
            print(baseStr + "Connection Failure: " + responseOr.getErrorMessage())
            return
        }
        let response = responseOr.get()
        if (!response.ok()) {
            // Handle server error
            print(baseStr + "ServerStatusCode: " + String(describing: response.serverStatusCode))
            return
        }
        
        let newPosts = response.posts
        let newMetdata = response.queryMetadata
        fetchPostsMetadata.updateMetadata(newMetadata: response.queryMetadata)
        print(baseStr + "Fetched " + String(newPosts.count) + " posts around user")
    
        self.postFeedManager.addMorePosts(morePosts: newPosts, newMetadata: newMetdata)
        DispatchQueue.main.async {
            self.postFeedManager.reload()
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
        client.getAllPostsAtLocation(location: self.getTestLocation(),
                                     queryParams: queryParams,
                                     completion:fetchPostsAroundUserCompletion)
    }
    
    func likePost(post: Post) {
    }
    
    func dislikePost(post: Post) {
    }
}

//

