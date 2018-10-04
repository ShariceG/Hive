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
    
    init(username: String, postId: String, postText: String, location: String, likes: Int, dislikes: Int) {
        self.username = username
        self.postId = postId
        self.postText = postText
        self.location = location
        self.likes = likes
        self.dislikes = dislikes
    }
    
}
