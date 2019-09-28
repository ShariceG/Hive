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
    private(set) var likes: Int
    private(set) var dislikes: Int
    private(set) var creationTimestampSec: Decimal
    private(set) var jsonPost: [String: Any]?
    
    func hash(into hasher: inout Hasher) {
        hasher.combine(postId)
    }
    
    static func ==(left:Post, right:Post) -> Bool {
        return left.postId == right.postId
    }
    
    init(username: String, postId: String, postText: String, locationJson: [String: Any], likes: Int, dislikes: Int, creationTimestampSec: Decimal, jsonPost: [String: Any]) {
        self.username = username
        self.postId = postId
        self.postText = postText
        self.location = Location.jsonToLocation(json: locationJson)
        self.likes = likes
        self.dislikes = dislikes
        self.creationTimestampSec = creationTimestampSec
        self.jsonPost = jsonPost
    }
    
    public func isExpired() -> Bool {
        let currentTimeSec: Double = Date().getCurrentTimeSec() / 1000
        return Double(truncating: self.creationTimestampSec as NSNumber) + Post.POST_LIFESPAN_SEC < currentTimeSec
    }
    
    public func toString() -> String {
        let str1: String = "Username: " + username
        + "\nPostID: " + postId
        + "\nPostText: " + postText
        
        let str2: String = "\nLocation: " + location.area
            + "\nLikes: " + String(likes)
        
        return str1 + str2 + "\nDislikes: " + String(dislikes) + "\n"
    }
    
    public static func jsonToPost(jsonPost: [String: Any]) -> Post {
        let likes = jsonPost["likes"] == nil ? 0 : Int(jsonPost["likes"] as! String)
        let dislikes = jsonPost["dislikes"] == nil ? 0 : Int(jsonPost["dislikes"] as! String)
        return Post(username: jsonPost["username"] as! String,
                    postId: jsonPost["post_id"] as! String,
                    postText: jsonPost["post_text"] as! String,
                    locationJson: jsonPost["location"] as! [String: Any],
                    likes: likes!,
                    dislikes: dislikes!,
                    creationTimestampSec: (jsonPost["creation_timestamp_sec"] as! NSNumber).decimalValue,
                    jsonPost: jsonPost)
    }
}
