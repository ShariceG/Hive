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
    
    private(set) var posts: Array<Post>
    private(set) var comments: Array<Comment>
    private(set) var serverStatusCode: ServerStatusCode
    private(set) var locations: Array<Location>
    private(set) var queryMetadata: QueryMetadata
    
    init(jsonObject: [String: Any]) {
        // Must initialize all member variables before calling class helper functions
        self.posts = []
        self.comments = []
        self.serverStatusCode = ServerStatusCode.UNKNOWN_ERROR
        self.locations = []
        self.queryMetadata = QueryMetadata()

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
            self.locations = getLocationList(jsonLocations: jsonObject["locations"] as! Array<Any>)
        }
        if (jsonObject["query_metadata"] != nil) {
            self.queryMetadata = QueryMetadata(jsonMetadata: jsonObject["query_metadata"] as! [String : Any])
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
    
    private func getLocationList(jsonLocations: Array<Any>) -> Array<Location> {
        var locations: Array<Location> = []
        for location in jsonLocations {
            let locString = location as! String
            locations.append(Location(locationStr: locString))
        }
        
        for location in locations {
            location.waitUntilGeoLocationIsReversed()
            print("LMAO: " + location.label)
        }
        return locations
    }
}
