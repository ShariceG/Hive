//
//  Comment.swift
//  Hive
//
//  Created by Chuck Onwuzuruike on 10/4/18.
//  Copyright © 2018 Chuck Onwuzuruike. All rights reserved.
//

import Foundation

class Comment {
    private(set) var commentId: String
    // Username of author of comment.
    private(set) var username: String
    private(set) var postId: String
    private(set) var commentText: String
    
    init(commentId: String, username: String, postId: String, commentText: String) {
        self.commentId = commentId
        self.username = username
        self.postId = postId
        self.commentText = commentText
    }
}
