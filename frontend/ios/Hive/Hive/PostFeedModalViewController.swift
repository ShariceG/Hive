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
    @IBOutlet weak var areaLabel: UILabel!
    
    @IBOutlet weak var postFeedTableView: UITableView!
    private var postFeedManager: PostFeedManager = PostFeedManager()
    private(set) var client: ServerClient = ServerClient()
    private var location: Location?

    override func viewDidLoad() {
        setupSwipeGestures()
        postFeedManager.configure(tableView: postFeedTableView, delegate: self)
        self.areaLabel.text =
            location!.area.city + ", " + location!.area.country
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
    
    private func getPopularPostsFromLocationCompletion(responseOr: StatusOr<Response>, notes: [String:Any]?) {
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
        postFeedManager.addMorePosts(morePosts: responseOr.get().posts, newMetadata: responseOr.get().queryMetadata)
        DispatchQueue.main.async {
            self.postFeedManager.reloadUI()
        }
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if (segue.identifier == COMMENTS_SEGUE_IDENTIFIER) {
            let postView: PostView = sender as! PostView
            let commentsViewController: CommentsViewController = segue.destination as! CommentsViewController
            commentsViewController.controllerInit(post: postView.post,
                                                  disallowInteractingWithComments:  true)
            return
        }
    }
    
}


extension PostFeedModalViewController: PostFeedDelegate {
    
    func showComments(postView: PostView) {
        self.performSegue(withIdentifier: COMMENTS_SEGUE_IDENTIFIER, sender: postView)
    }
    
    func fetchMorePosts(queryParams: QueryParams) {
        client.getAllPopularPostsAtLocation(username: self.getLoggedInUsername(), queryParams: queryParams,
                                            location: self.location!,
                                            completion: getPopularPostsFromLocationCompletion, notes: nil)
    }
    
    func performAction(post: Post, actionType: ActionType) {
    }
}

