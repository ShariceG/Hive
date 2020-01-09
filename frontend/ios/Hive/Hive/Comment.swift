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
    private(set) var creationTimestampSec: Decimal
    private(set) var jsonComment: [String: Any]?
    // Type of action requesting user did on this post.
    // Can be mutated. Let's keep the amount of mutable things
    // small.
    public var userActionType: ActionType
    public var likes: Int
    public var dislikes: Int
    
    init(commentId: String, username: String, postId: String, commentText: String, creationTimestampSec: Decimal,  likes: Int, dislikes: Int, userActionType: ActionType) {
        self.commentId = commentId
        self.username = username
        self.postId = postId
        self.commentText = commentText
        self.creationTimestampSec = creationTimestampSec
        self.jsonComment = Optional.none
        self.likes = likes
        self.dislikes = dislikes
        self.userActionType = userActionType
    }
    
    convenience init(commentId: String, username: String, postId: String, commentText: String,
                     creationTimestampSec: Decimal,
                     jsonComment: [String: Any], likes: Int, dislikes: Int, userActionType: ActionType) {
        self.init(commentId: commentId, username: username, postId: postId, commentText: commentText,
                  creationTimestampSec: creationTimestampSec, likes: likes, dislikes: dislikes, userActionType: userActionType)
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
        let likes = jsonComment["likes"] == nil ? 0 : Int(jsonComment["likes"] as! String)
        let dislikes = jsonComment["dislikes"] == nil ? 0 : Int(jsonComment["dislikes"] as! String)
        let userActionType = jsonComment["user_action_type"] == nil ? ActionType.NO_ACTION :
            ActionType.FromString(str: jsonComment["user_action_type"] as! String)
        return Comment(commentId: jsonComment["comment_id"]  as! String,
                       username: jsonComment["username"] as! String,
                       postId: jsonComment["post_id"] as! String,
                       commentText: jsonComment["comment_text"] as! String,
        creationTimestampSec: (jsonComment["creation_timestamp_sec"] as! NSNumber).decimalValue,
        jsonComment: jsonComment, likes: likes!, dislikes: dislikes!, userActionType: userActionType)
    }
}
