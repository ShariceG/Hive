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
    private(set) var jsonComment: [String: Any]?
    
    init(commentId: String, username: String, postId: String, commentText: String) {
        self.commentId = commentId
        self.username = username
        self.postId = postId
        self.commentText = commentText
        self.jsonComment = Optional.none
    }
    
    convenience init(commentId: String, username: String, postId: String, commentText: String,
                     jsonComment: [String: Any]) {
        self.init(commentId: commentId, username: username, postId: postId, commentText: commentText)
        self.jsonComment = jsonComment
    }
    
    public func toString() -> String {
        let str1: String = "Username: " + username
            + "\nPostID: " + postId
            + "\nCommentId: " + String(commentId)
        
        let str2: String = "\nCommentText: " + commentText
        
        return str1 + str2 + "\n"
    }
    
    public static func jsonToComment(jsonComment: [String: Any]) -> Comment {
        return Comment(commentId: jsonComment["comment_id"]  as! String,
                       username: jsonComment["username"] as! String,
                       postId: jsonComment["post_id"] as! String,
                       commentText: jsonComment["comment_text"] as! String)
    }
}
