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
    
    @IBOutlet weak var postView: UIView!
    private(set) weak var postViewTableCell: PostViewTableViewCell? = nil
    private var post: Post? = nil
    
    private let client: ServerClient = ServerClient()
    private var allPostComments: Array<Comment> = []
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.postViewTableCell?.commentBn.isEnabled = false
        self.post = self.postViewTableCell?.post
        postView.addSubview(self.postViewTableCell!)
    }
    
    private func getAllPostComments() {
        client.getAllCommentsForPost(postId: (post?.postId)!, completion: getAllPostCommentsCompletion)
    }
    
    private func getAllPostCommentsCompletion(response: StatusOr<Response>) {
        if (response.hasError()) {
            // Handle likley connection error
            print("Connection Failure: " + response.getErrorMessage())
            return
        }
        if (response.get().serverStatusCode != ServerStatusCode.OK) {
            // Handle server error
            print("ServerStatusCode: " + String(describing: response.get().serverStatusCode))
            return
        }
        allPostComments.removeAll()
        allPostComments.append(contentsOf: response.get().comments)
        
//        DispatchQueue.main.async {
//            self.postTableView.reloadData()
//        }
    }
    
    public func shareData(postViewTableCell: PostViewTableViewCell) {
        self.postViewTableCell = postViewTableCell
    }
    
}
