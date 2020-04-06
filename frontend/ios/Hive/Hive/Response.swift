//
//  Response.swift
//  Hive
//
//  Created by Chuck Onwuzuruike on 10/4/18.
//  Copyright © 2018 Chuck Onwuzuruike. All rights reserved.
//

import Foundation
import CoreLocation

class QueryMetadata {
    var newTopCursorStr: String?
    var newBottomCursorStr: String?
    var hasMoreOlderData: Bool?
    
    init() {
        newTopCursorStr = nil
        newBottomCursorStr = nil
        hasMoreOlderData = nil
    }
   
    init(jsonMetadata: [String: Any]) {
        if jsonMetadata["new_top_cursor_str"] != nil {
            newTopCursorStr = jsonMetadata["new_top_cursor_str"] as? String
        }
        if jsonMetadata["new_bottom_cursor_str"] != nil {
            newBottomCursorStr = jsonMetadata["new_bottom_cursor_str"] as? String

        }
        if jsonMetadata["has_more_older_data"] != nil {
            hasMoreOlderData = jsonMetadata["has_more_older_data"] as? Bool
        }
    }
    
    func updateMetadata(newMetadata: QueryMetadata) {
        if (newMetadata.newTopCursorStr != nil) {
            newTopCursorStr = newMetadata.newTopCursorStr
        }
        if (newMetadata.newBottomCursorStr != nil) {
            newBottomCursorStr = newMetadata.newBottomCursorStr
        }
        if (newMetadata.hasMoreOlderData != nil) {
            hasMoreOlderData = newMetadata.hasMoreOlderData
        }
    }
}

class Response {
    
    // Yes, oky, not a great solution but it works.
    // All the possible things we can get as a response from the server.
    // Whoever makes the request looks and gets what it expects. Simple.
    // Let's try and keep this as minimal as possible.
    private(set) var posts: Array<Post>
    private(set) var comments: Array<Comment>
    private(set) var serverStatusCode: ServerStatusCode
    private(set) var locations: Array<Location>
    private(set) var queryMetadata: QueryMetadata
    private(set) var verificationCode: String
    private(set) var username: String
    
    init(jsonObject: [String: Any]) {
        // Must initialize all member variables before calling class helper functions
        self.posts = []
        self.comments = []
        self.serverStatusCode = ServerStatusCode.UNKNOWN_ERROR
        self.locations = []
        self.queryMetadata = QueryMetadata()
        self.verificationCode = ""
        self.username = ""

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
        if (jsonObject["locations"] != nil) {
            self.locations = getLocationList(jsonLocations: jsonObject["locations"] as! [[String : Any]])
        }
        if (jsonObject["query_metadata"] != nil) {
            self.queryMetadata = QueryMetadata(jsonMetadata: jsonObject["query_metadata"] as! [String : Any])
        }
        if (jsonObject["verification_code"] != nil) {
            self.verificationCode = jsonObject["verification_code"] as! String
        }
        if (jsonObject["username"] != nil) {
            self.username = jsonObject["username"] as! String
        }
    }
    
    public func firstPost() -> Optional<Post> {
        return posts.isEmpty ? Optional.none : Optional<Post>(posts[0])
    }
    
    public func firstComment() -> Optional<Comment> {
        return comments.isEmpty ? Optional.none : Optional<Comment>(comments[0])
    }
    
    public func ok() -> Bool {
        return serverStatusCode == ServerStatusCode.OK
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
    
    private func getLocationList(jsonLocations: [[String: Any]]) -> Array<Location> {
        var locations: Array<Location> = []
        for locationJson in jsonLocations {
            locations.append(Location(locationJson: locationJson))
        }
        return locations
    }
}
