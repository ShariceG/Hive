//
//  Response.swift
//  Hive
//
//  Created by Chuck Onwuzuruike on 10/4/18.
//  Copyright © 2018 Chuck Onwuzuruike. All rights reserved.
//

import Foundation

class Response {
    
    private(set) var posts: Array<Post>
    private(set) var comments: Array<Comment>
    private(set) var serverStatusCode: ServerStatusCode
    
    init(posts: Array<Post>, comments: Array<Comment>, code: ServerStatusCode) {
        self.posts = posts
        self.comments = comments
        self.serverStatusCode = code
    }
    
    init(jsonObject: [String: Any]) {
        // Must initialize all member variables before calling class helper functions
        self.posts = []
        self.comments = []
        self.serverStatusCode = ServerStatusCode.UNKNOWN_ERROR

        if (jsonObject["posts"] != nil) {
            self.posts = getPostList(jsonPosts: jsonObject["posts"] as! [[String:Any]])
        }
        if (jsonObject["comments"] != nil) {
            self.comments = getCommentList(jsonComments: jsonObject["comments"] as! [[String:Any]])
        }
        if (jsonObject["status"] != nil &&
            (jsonObject["status"] as! [String:Any])["status_code"] != nil) {
            self.serverStatusCode = getServerStatusCodeFromJson(jsonObject: jsonObject)
        }
    }
    
    public func firstPost() -> Optional<Post> {
        return posts.isEmpty ? Optional.none : Optional<Post>(posts[0])
    }
    
    public func firstComment() -> Optional<Comment> {
        return comments.isEmpty ? Optional.none : Optional<Comment>(comments[0])
    }
    
    private func getServerStatusCodeFromJson(jsonObject: [String: Any]) -> ServerStatusCode {
        let serverStatusCodeStr = (jsonObject["status"] as! [String:Any])["status_code"] as! String
        return ServerStatusCode.enumFromString(string: serverStatusCodeStr)!
    }
    
    private func getPostList(jsonPosts: [[String: Any]]) -> Array<Post> {
        var postList: Array<Post> = []
        for jsonPost in jsonPosts {
            postList.append(Post.jsonToPost(jsonPost: jsonPost))
        }
        return postList
    }
    
    private func getCommentList(jsonComments: [[String: Any]]) -> Array<Comment> {
        var commentList: Array<Comment> = []
        for jsonComment in jsonComments {
            commentList.append(Comment.jsonToComment(jsonComment: jsonComment))
        }
        return commentList
    }
}