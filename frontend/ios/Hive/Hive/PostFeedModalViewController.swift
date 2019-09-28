//
//  PostFeedModalViewController.swift
//  Hive
//
//  Created by Chuck Onwuzuruike on 9/22/19.
//  Copyright © 2019 Chuck Onwuzuruike. All rights reserved.
//

import Foundation
import UIKit

class PostFeedModalViewController: UIViewController {
    
    let COMMENTS_SEGUE_IDENTIFIER = "seeCommentsSegue"
    
    @IBOutlet weak var postFeedTableView: UITableView!
    private var postFeedManager: PostFeedManager = PostFeedManager()
    private(set) var client: ServerClient = ServerClient()
    private var location: Location?

    override func viewDidLoad() {
        setupSwipeGestures()
        postFeedManager.configure(tableView: postFeedTableView, delegate: self)
    }
    
    func controllerInit(location: Location) {
        self.location = location
    }
    
    private func setupSwipeGestures() {
        let swipeDown = UISwipeGestureRecognizer(
            target: self, action: #selector(swipeGestureAction))
        swipeDown.direction = .down
        self.view?.addGestureRecognizer(swipeDown)
    }
    
    @objc func swipeGestureAction(recognizer: UISwipeGestureRecognizer) {
        switch recognizer.direction {
        case UISwipeGestureRecognizer.Direction.down:
            _ = self.navigationController?.popViewController(animated: true)
            self.dismiss(animated: true, completion: nil)
        default:
            break
        }
    }
    
    private func getPopularPostsFromLocationCompletion(responseOr: StatusOr<Response>) {
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
        
        postFeedManager.addMorePosts(morePosts: responseOr.get().posts, newMetadata: responseOr.get().queryMetadata)
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


extension PostFeedModalViewController: PostFeedDelegate {
    func showComments(postView: PostView) {
        self.performSegue(withIdentifier: COMMENTS_SEGUE_IDENTIFIER, sender: postView)
    }
    
    func fetchMorePosts(queryParams: QueryParams) {
        client.getAllPopularPostsAtLocation(username: self.getTestUser(), queryParams: queryParams,
                                            location: self.location!,
            completion: getPopularPostsFromLocationCompletion)
    }
    
    func likePost(post: Post) {
    }
    
    func dislikePost(post: Post) {
    }
}

