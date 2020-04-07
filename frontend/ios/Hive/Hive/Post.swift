//
//  Post.swift
//  Hive
//
//  Created by Chuck Onwuzuruike on 10/4/18.
//  Copyright © 2018 Chuck Onwuzuruike. All rights reserved.
//

import Foundation

class Post: Hashable, Equatable {
    private static let POST_LIFESPAN_SEC : Double = 24 * 60 * 60  // 24 hours
    private(set) var username: String
    private(set) var postId: String
    private(set) var postText: String
    private(set) var location: Location
    private(set) var creationTimestampSec: Decimal
    private(set) var jsonPost: [String: Any]?
    private(set) var numberOfComments: Int
    // Type of action requesting user did on this post.
    // Can be mutated. Let's keep the amount of mutable things
    // small.
    public var userActionType: ActionType
    public var likes: Int
    public var dislikes: Int
    
    func hash(into hasher: inout Hasher) {
        hasher.combine(postId)
    }
    
    static func ==(left:Post, right:Post) -> Bool {
        return left.postId == right.postId
    }
    
    init(username: String, postId: String, postText: String, locationJson: [String: Any], likes: Int, dislikes: Int, creationTimestampSec: Decimal, userActionType: ActionType,
         numberOfComments: Int, jsonPost: [String: Any]) {
        self.username = username
        self.postId = postId
        self.postText = postText
        self.location = Location.jsonToLocation(json: locationJson)
        self.likes = likes
        self.dislikes = dislikes
        self.creationTimestampSec = creationTimestampSec
        self.userActionType = userActionType
        self.numberOfComments = numberOfComments
        self.jsonPost = jsonPost
    }
    
    public func isExpired() -> Bool {
        let currentTimeSec: Double = Date().getCurrentTimeSec()
        return Double(truncating: self.creationTimestampSec as NSNumber) + Post.POST_LIFESPAN_SEC < currentTimeSec
    }
    
    public func toString() -> String {
        let str1: String = "Username: " + username
        + "\nPostID: " + postId
        + "\nPostText: " + postText
        
        let str2: String = "\nLocation: " + location.area.toString()
            + "\nLikes: " + String(likes)
        
        return str1 + str2 + "\nDislikes: " + String(dislikes) + "\n"
    }
    
    public static func jsonToPost(jsonPost: [String: Any]) -> Post {
        let likes = jsonPost["likes"] == nil ? 0 : Int(jsonPost["likes"] as! String)
        let dislikes = jsonPost["dislikes"] == nil ? 0 : Int(jsonPost["dislikes"] as! String)
        let userActionType = jsonPost["user_action_type"] == nil ? ActionType.NO_ACTION :
            ActionType.FromString(str: jsonPost["user_action_type"] as! String)
        let numberOfComments = jsonPost["number_of_comments"] == nil ? 0 :
            Int(jsonPost["number_of_comments"] as! String)
        return Post(username: jsonPost["username"] as! String,
                    postId: jsonPost["post_id"] as! String,
                    postText: jsonPost["post_text"] as! String,
                    locationJson: jsonPost["location"] as! [String: Any],
                    likes: likes!,
                    dislikes: dislikes!,
                    creationTimestampSec: (jsonPost["creation_timestamp_sec"] as! NSNumber).decimalValue,
                    userActionType: userActionType,
                    numberOfComments: numberOfComments!,
                    jsonPost: jsonPost)
    }
    
    private func getTimeDifferenceSec() -> Int{
        let postDateSec = Int(truncating: self.creationTimestampSec as NSNumber)
        let currDate = Int(Date().getCurrentTimeSec())
        let diff = currDate - postDateSec
        print(diff)
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
