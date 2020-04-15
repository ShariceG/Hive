//
//  Comment.swift
//  Hive
//
//  Created by Chuck Onwuzuruike on 10/4/18.
//  Copyright © 2018 Chuck Onwuzuruike. All rights reserved.
//

import Foundation

class Comment: Hashable, Equatable {
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
    
    func hash(into hasher: inout Hasher) {
        hasher.combine(commentId)
    }
    
    static func ==(left:Comment, right:Comment) -> Bool {
        return left.commentId == right.commentId
    }
    
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
    
    private func getTimeDifferenceSec() -> Int{
           let postDateSec = Int(truncating: self.creationTimestampSec as NSNumber)
           let currDate = Int(Date().getCurrentTimeSec())
           let diff = currDate - postDateSec
           return diff
       }
       
       public func timeDiffToString() -> String {
           let diffInSec = self.getTimeDifferenceSec()
           let days =  diffInSec/86400
           let hours = diffInSec/3600
           let minutes = diffInSec/60
           
           var time = 0
           var numString = ""
           var timePercisionString = ""

           if days >= 1 {
               time = days
               numString = String(days)
               timePercisionString = " days"
           } else if hours >= 1{
               time =  hours
               numString = String(hours)
               timePercisionString = " hours"
           } else if minutes >= 1{
               time =  minutes
               numString = String(minutes)
               timePercisionString = " minutes"
           } else {
               time =  diffInSec
               numString = String(diffInSec)
               timePercisionString = " seconds"
           }
           
           return numString + UtilityBelt.pluralOrSingular(num: time, word: timePercisionString) + " ago"
       }
}
