//
//  ServerMessenger.swift
//  Hive
//
//  Created by Chuck Onwuzuruike on 10/4/18.
//  Copyright © 2018 Chuck Onwuzuruike. All rights reserved.
//

import Foundation

class ServerClient {
    
    private static let CONNECTION_TIMEOUT_MS: Int = 5000
    private static let READ_TIMEOUT_MS: Int = 55000
    
//    private static let SERVER_DOMAIN: String = "http://localhost:8080"
    private static let SERVER_DOMAIN: String = "http://10.0.0.52:8080"
    private static let COMMON_PATH: String = "/_ah/api/media_api/v1/"
    private static let CREATE_USER_PATH: String = "app.create_user?"
    private static let INSERT_COMMENT_PATH: String = "app.insert_comment?"
    private static let INSERT_POST_PATH: String = "app.insert_post?"
    private static let GET_ALL_POST_COMMENTS_PATH: String = "app.get_all_comments_for_post?"
    private static let GET_ALL_POSTS_AROUND_USER_PATH: String = "app.get_all_posts_around_user?"
    private static let GET_ALL_POSTS_BY_USER_PATH: String = "app.get_all_posts_by_user?"
    private static let GET_ALL_POSTS_COMMENTED_ON_BY_USER_PATH: String = "app.get_all_posts_commented_on_by_user?"
    private static let UPDATE_POST_PATH: String = "app.update_post?"
    
    public func getAllPostsCommentedOnByUser(username: String, completion:@escaping (StatusOr<Response>) -> ()){
        var user: [String:Any] = [String:Any]()
        user["username"] = username
        var request: [String:Any] = [String:Any]()
        request["user"] = user
        let path = constructIncompleteUrlPath() + ServerClient.GET_ALL_POSTS_COMMENTED_ON_BY_USER_PATH
        
        executeGet(targetUrl: path, jsonParams: request, completion: completion)
    }
    
    public func getAllPostsByUser(username: String, completion:@escaping (StatusOr<Response>) -> ()) {
        var user: [String:Any] = [String:Any]()
        user["username"] = username
        var request: [String:Any] = [String:Any]()
        request["user"] = user
    
        let path: String = constructIncompleteUrlPath() + ServerClient.GET_ALL_POSTS_BY_USER_PATH
        executeGet(targetUrl: path, jsonParams: request, completion: completion)
    }
    
    public func getAllPostsAroundUser(username: String, location: String,
                                      before: Decimal?, after: Decimal?,
                                      completion:@escaping (StatusOr<Response>) -> ()) {
        var user: [String:Any] = [String:Any]()
        user["username"] = username
        user["location"] = location
        var request: [String:Any] = [String:Any]()
        request["user"] = user
        if (before != nil) {
            print("Sending THIS over: " + before!.description)
            request["timestamp_before_sec"] = before!.description
        }
        if (after != nil) {
            request["timestamp_after_sec"] = after!.description
        }

        let path: String = constructIncompleteUrlPath() + ServerClient.GET_ALL_POSTS_AROUND_USER_PATH
        executeGet(targetUrl: path, jsonParams: request, completion: completion)
    }
    
    public func getAllCommentsForPost(postId: String, completion:@escaping (StatusOr<Response>) -> ()) {
        var post: [String:Any] = [String:Any]()
        post["post_id"] = postId
        var request: [String:Any] = [String:Any]()
        request["post"] = post
    
        let path: String = constructIncompleteUrlPath() + ServerClient.GET_ALL_POST_COMMENTS_PATH
        executeGet(targetUrl: path, jsonParams: request, completion: completion)
    }
    
    public func createUser(username: String, phoneNumber: String, completion:@escaping (StatusOr<Response>) -> ()) {
        var user: [String:Any] = [String:Any]()
        user["username"] = username
        user["phone_number"] = phoneNumber
        var request: [String:Any] = [String:Any]()
        request["user"] = user
    
        let path: String = constructIncompleteUrlPath() + ServerClient.CREATE_USER_PATH
        executePost(targetUrl: path, jsonParams: request, completion: completion)
    }
    
    public func updatePost(postId: String, username: String, actionType: String, completion:@escaping (StatusOr<Response>) -> ()) {
        var user: [String:Any] = [String:Any]()
        user["username"] = username
        var post: [String:Any] = [String:Any]()
        post["post_id"] = postId
        var request: [String:Any] = [String:Any]()
        request["user"] = user
        request["post"] = post
        request["action_type"] = actionType
    
        let path: String = constructIncompleteUrlPath() + ServerClient.UPDATE_POST_PATH
        executePost(targetUrl: path, jsonParams: request, completion: completion)
    }
    
    public func insertPost(username: String, postText: String, location: String, completion:@escaping (StatusOr<Response>) -> ()) {
        var post: [String:Any] = [String:Any]()
        post["username"] = username
        post["post_text"] = postText
        post["location"] = location
        var request: [String:Any] = [String:Any]()
        request["post"] = post
    
        let path: String = constructIncompleteUrlPath() + ServerClient.INSERT_POST_PATH;
        executePost(targetUrl: path, jsonParams: request, completion: completion);
    }

    public func insertComment(username: String, commentText: String, postId: String, completion:@escaping (StatusOr<Response>) -> ()) {
        var comment: [String:Any] = [String:Any]()
        comment["username"] = username
        comment["comment_text"] = commentText
        comment["post_id"] = postId
        var request: [String:Any] = [String:Any]()
        request["comment"] = comment
    
        let path: String = constructIncompleteUrlPath() + ServerClient.INSERT_COMMENT_PATH;
        executePost(targetUrl: path, jsonParams: request, completion: completion);
    }
    
    private func constructIncompleteUrlPath() -> String {
        return ServerClient.SERVER_DOMAIN + ServerClient.COMMON_PATH;
    }
    
    private func jsonObjectToUrlParameters(jsonRequest: [String:Any]) -> String {
        let urlParams: String = jsonObjectToUrlParametersImp(jsonObject: jsonRequest, prefix: "");
        return String(urlParams.characters.dropLast())
    }
    
    private func jsonObjectToUrlParametersImp(jsonObject: [String:Any], prefix: String) -> String {
        var params: String = "";
        for (key, value) in jsonObject {
            let newPrefix: String = prefix.isEmpty ? key : prefix + "." + key
            if (value as? String != nil) {
                params += newPrefix + "=" + (value as! String) + "&";
            } else {
                params += jsonObjectToUrlParametersImp(jsonObject: value as! [String:Any], prefix: newPrefix);
            }
        }
        return params;
    }
    
    // Literally have no idea how this function really works.
    private func executeGet(targetUrl: String, jsonParams: [String:Any], completion: @escaping (StatusOr<Response>) -> ()) {
        var url: String = targetUrl
        url += jsonObjectToUrlParameters(jsonRequest: jsonParams)
        
        //create the url with NSURL
        let urlObj = URL(string: url)!
        
        //create the session object
        let session = URLSession.shared
        
        //now create the URLRequest object using the url object
        let request = URLRequest(url: urlObj)
        
        //create dataTask using the session object to send data to the server
        let task = session.dataTask(with: request as URLRequest, completionHandler: { data, response, error in
            
            guard let data = data, error == nil else {
                completion(StatusOr<Response>(err: StatusError.GENERIC_CONNECTION_ERROR, msg: String(describing: error!)))
                return
            }
            
            let httpStatus = response as? HTTPURLResponse
            let httpStatusCode:Int = (httpStatus?.statusCode)!
            
            do {
                //create json object from data
                let json = try JSONSerialization.jsonObject(with: data, options: .mutableContainers) as? [String: Any]
                if json == nil {
                    completion(StatusOr<Response>(err: StatusError.GENERIC_EMPTY_ERROR,
                                                  msg: "Expected non-empty response. No or invalid JSON to parse. \nHTTP Code: " + String(httpStatusCode)
                    + " -> \nurl: " + url))
                }
                // Everything is okay at this point.
                completion(StatusOr<Response>(v: Response(jsonObject: json!)))
            } catch let error {
                completion(StatusOr<Response>(err: StatusError.GENERIC_CONNECTION_ERROR, msg: String(describing: error) + "\nHTTP Code: " + String(httpStatusCode)
                    + " -> \nurl: " + url))
            }
        })
        task.resume()
    }
    
    // Also, literally have no idea how this function really works.
    private func executePost(targetUrl: String, jsonParams: [String:Any], completion: @escaping (StatusOr<Response>) -> ()) {
        let jsonData: Data = try! JSONSerialization.data(withJSONObject: jsonParams, options: [])
        let jsonString: String = String(data: jsonData, encoding: .utf8)!
        let url = URL(string: targetUrl)
        var request = URLRequest(url: url!)
        
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        request.httpMethod = "POST"
        let postString = jsonString
        request.httpBody = postString.data(using: .utf8)
        
        let task = URLSession.shared.dataTask(with: request) { data, response, error in
            guard let data = data, error == nil else {
                completion(StatusOr<Response>(err: StatusError.GENERIC_CONNECTION_ERROR, msg: String(describing: error!)))
                return
            }
            
            let httpStatus = response as? HTTPURLResponse
            let httpStatusCode:Int = (httpStatus?.statusCode)!
            
            do {
                //create json object from data
                let json = try JSONSerialization.jsonObject(with: data, options: .mutableContainers) as? [String: Any]
                if json == nil {
                    completion(StatusOr<Response>(err: StatusError.GENERIC_EMPTY_ERROR,
                                                  msg: "Expected non-empty response. No or invalid JSON to parse. \nHTTP Code: " + String(httpStatusCode)
                                                    + " -> \nurl: " + targetUrl))
                }
                // Everything is okay at this point.
                completion(StatusOr<Response>(v: Response(jsonObject: json!)))
            } catch let error {
                completion(StatusOr<Response>(err: StatusError.GENERIC_CONNECTION_ERROR, msg: String(describing: error) + "\nHTTP Code: " + String(httpStatusCode)
                    + " -> \nurl: " + targetUrl))
            }
        }
        task.resume()
    }
}
