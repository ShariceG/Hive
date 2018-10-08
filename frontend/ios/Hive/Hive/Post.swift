//
//  Post.swift
//  Hive
//
//  Created by Chuck Onwuzuruike on 10/4/18.
//  Copyright © 2018 Chuck Onwuzuruike. All rights reserved.
//

import Foundation

class Post {
    private(set) var username: String
    private(set) var postId: String
    private(set) var postText: String
    private(set) var location: String
    private(set) var likes: Int
    private(set) var dislikes: Int
    private(set) var jsonPost: [String: Any]?
    
    init(username: String, postId: String, postText: String, location: String, likes: Int, dislikes: Int) {
        self.username = username
        self.postId = postId
        self.postText = postText
        self.location = location
        self.likes = likes
        self.dislikes = dislikes
        self.jsonPost = Optional.none
    }
    
    convenience init(username: String, postId: String, postText: String, location: String, likes: Int, dislikes: Int, jsonPost: [String: Any]) {
        self.init(username: username,
                  postId: postId,
                  postText: postText,
                  location: location,
                  likes: likes,
                  dislikes: dislikes)
        self.jsonPost = jsonPost
    }
    
    public func toString() -> String {
        let str1: String = "Username: " + username
        + "\nPostID: " + postId
        + "\nPostText: " + postText
        
        let str2: String = "\nLocation: " + location
            + "\nLikes: " + String(likes)
        
        return str1 + str2 + "\nDislikes: " + String(dislikes) + "\n"
    }
    
    public static func jsonToPost(jsonPost: [String: Any]) -> Post {
        let likes = jsonPost["likes"] == nil ? 0 : Int(jsonPost["likes"] as! String)
        let dislikes = jsonPost["dislikes"] == nil ? 0 : Int(jsonPost["dislikes"] as! String)
        return Post(username: jsonPost["username"] as! String,
                    postId: jsonPost["post_id"] as! String,
                    postText: jsonPost["post_text"] as! String,
                    location: jsonPost["location"] as! String,
                    likes: likes!,
                    dislikes: dislikes!,
                    jsonPost: jsonPost)
    }
}
