//
//  ServerMessenger.swift
//  Hive
//
//  Created by Chuck Onwuzuruike on 10/4/18.
//  Copyright © 2018 Chuck Onwuzuruike. All rights reserved.
//

import Foundation

class QueryParams {
    public private(set) var getNewer: Bool?
    public private(set) var currTopCursorStr: String?
    public private(set) var currBottomCursorStr: String?

    init() {
        getNewer = true
        currTopCursorStr = ""
        currBottomCursorStr = ""
    }

    init(getNewer: Bool?, currTopCursorStr: String?, currBottomCursorStr: String?) {
        self.getNewer = getNewer
        self.currTopCursorStr = currTopCursorStr
        self.currBottomCursorStr = currBottomCursorStr
    }

    func toJson() -> [String:Any] {
        var params: [String:Any] = [String:Any]()
        if (getNewer != nil) {
            params["get_newer"] = getNewer! ? "true" : "false"
        }
        if (currTopCursorStr != nil) {
            params["curr_top_cursor_str"] = currTopCursorStr!
        }
        if (currBottomCursorStr != nil) {
            params["curr_bottom_cursor_str"] = currBottomCursorStr!
        }
        return params
    }
}

class ServerClient {

    private static let CONNECTION_TIMEOUT_MS: Int = 5000
    private static let READ_TIMEOUT_MS: Int = 55000

    private static let SERVER_DOMAIN: String = "http://192.168.0.42:8080"
//    private static let SERVER_DOMAIN: String = "http://192.168.0.43:8080"
    private static let COMMON_PATH: String = "/_ah/api/media_api/v1/"
    private static let CREATE_USER_PATH: String = "app.create_user?"
    private static let VERIFY_CODE_PATH: String = "app.verify_code?"
    private static let INSERT_COMMENT_PATH: String = "app.insert_comment?"
    private static let INSERT_POST_PATH: String = "app.insert_post?"
    private static let GET_ALL_POST_COMMENTS_PATH: String = "app.get_all_comments_for_post?"
    private static let GET_ALL_POSTS_AROUND_USER_PATH: String = "app.get_all_posts_around_user?"
    private static let GET_ALL_POSTS_AT_LOCATION: String =
        "app.get_all_posts_at_location?"
    private static let GET_ALL_POST_LOCATIONS: String =
        "app.get_all_post_locations?"
    private static let GET_ALL_POSTS_BY_USER_PATH: String = "app.get_all_posts_by_user?"
    private static let GET_ALL_POSTS_COMMENTED_ON_BY_USER_PATH: String = "app.get_all_posts_commented_on_by_user?"
    private static let UPDATE_POST_PATH: String = "app.update_post?"
    private static let UPDATE_COMMENT_PATH: String = "app.update_comment?"
    private static let GET_ALL_POPULAR_POSTS_AT_LOCATION: String = "app.get_all_popular_posts_at_location?"
    private static let GET_POPULAR_LOCATIONS: String = "app.get_popular_locations?"

    public func getPopularLocations(completion:@escaping ((StatusOr<Response>, [String:Any]?) -> ()), notes: [String:Any]?) {
        let request: [String:Any] = [String:Any]()
        let path = constructIncompleteUrlPath() + ServerClient.GET_POPULAR_LOCATIONS
        executeGet(targetUrl: path, jsonParams: request, completion: completion, notes: notes)
    }

    public func getAllPostLocations(completion:@escaping ((StatusOr<Response>, [String:Any]?) -> ()), notes: [String:Any]?) {
        let request: [String:Any] = [String:Any]()
        let path = constructIncompleteUrlPath() + ServerClient.GET_ALL_POST_LOCATIONS
        executeGet(targetUrl: path, jsonParams: request, completion: completion, notes: notes)
    }

    public func getAllPostsAtLocation(username: String, location: Location, queryParams: QueryParams, completion:@escaping ((StatusOr<Response>, [String:Any]?) -> ()), notes: [String:Any]?) {
        var request: [String:Any] = [String:Any]()
        request["location"] = location.toJson()
        request["query_params"] = queryParams.toJson()
        request["username"] = username

        let path = constructIncompleteUrlPath() + ServerClient.GET_ALL_POSTS_AT_LOCATION
        executeGet(targetUrl: path, jsonParams: request, completion: completion, notes: notes)
    }

    public func getAllPopularPostsAtLocation(username: String, queryParams: QueryParams, location: Location, completion:@escaping ((StatusOr<Response>, [String:Any]?) -> ()), notes: [String:Any]?) {
        var request: [String:Any] = [String:Any]()
        request["username"] = username
        request["location"] = location.toJson()
        request["query_params"] = queryParams.toJson()

        let path = constructIncompleteUrlPath() + ServerClient.GET_ALL_POPULAR_POSTS_AT_LOCATION
        executeGet(targetUrl: path, jsonParams: request, completion: completion, notes: notes)
    }

    public func getAllPostsCommentedOnByUser(username: String, queryParams: QueryParams, completion:@escaping ((StatusOr<Response>, [String:Any]?) -> ()), notes: [String:Any]?){
        var request: [String:Any] = [String:Any]()
        request["username"] = username
        request["query_params"] = queryParams.toJson()

        let path = constructIncompleteUrlPath() + ServerClient.GET_ALL_POSTS_COMMENTED_ON_BY_USER_PATH
        executeGet(targetUrl: path, jsonParams: request, completion: completion, notes: notes)
    }

    public func getAllPostsByUser(username: String, queryParams: QueryParams, completion:@escaping ((StatusOr<Response>, [String:Any]?) -> ()), notes: [String:Any]?) {
        var request: [String:Any] = [String:Any]()
        request["username"] = username
        request["query_params"] = queryParams.toJson()

        let path: String = constructIncompleteUrlPath() + ServerClient.GET_ALL_POSTS_BY_USER_PATH
        executeGet(targetUrl: path, jsonParams: request, completion: completion, notes: notes)
    }

    public func getAllCommentsForPost(username: String,
                                      postId: String, queryParams: QueryParams, completion:@escaping ((StatusOr<Response>, [String:Any]?) -> ()), notes: [String:Any]?) {
        var request: [String:Any] = [String:Any]()
        request["post_id"] = postId
        request["query_params"] = queryParams.toJson()
        request["username"] = username

        let path: String = constructIncompleteUrlPath() + ServerClient.GET_ALL_POST_COMMENTS_PATH
        executeGet(targetUrl: path, jsonParams: request, completion: completion, notes: notes)
    }
    
    public func checkVerificationCode(username: String, email: String, code: String,
                                      completion:@escaping ((StatusOr<Response>, [String:Any]?) -> ()), notes: [String:Any]?) {
        var request: [String:Any] = [String:Any]()
        request["username"] = username
        request["email"] = email
        request["verification_code"] = code

        let path: String = constructIncompleteUrlPath() + ServerClient.VERIFY_CODE_PATH
        executePost(targetUrl: path, jsonParams: request, completion: completion, notes: notes)
    }
    
    public func verifyExistingUser(email: String,
            completion:@escaping ((StatusOr<Response>, [String:Any]?) -> ()), notes: [String:Any]?) {
        var request: [String:Any] = [String:Any]()
        request["email"] = email
        request["verify_email_only"] = true

        let path: String = constructIncompleteUrlPath() + ServerClient.CREATE_USER_PATH
        executePost(targetUrl: path, jsonParams: request, completion: completion, notes: notes)
    }
    
    public func createNewUser(username: String, email: String,
                                      completion:@escaping ((StatusOr<Response>, [String:Any]?) -> ()), notes: [String:Any]?) {
        var request: [String:Any] = [String:Any]()
        request["username"] = username
        request["email"] = email

        let path: String = constructIncompleteUrlPath() + ServerClient.CREATE_USER_PATH
        executePost(targetUrl: path, jsonParams: request, completion: completion, notes: notes)
    }

    public func updateComment(commentId: String, username: String, actionType: ActionType, completion:@escaping ((StatusOr<Response>, [String:Any]?) -> ()), notes: [String:Any]?) {
        var request: [String:Any] = [String:Any]()
        request["username"] = username
        request["comment_id"] = commentId
        request["action_type"] = ActionType.ToString(actionType: actionType)

        let path: String = constructIncompleteUrlPath() + ServerClient.UPDATE_COMMENT_PATH
        executePost(targetUrl: path, jsonParams: request, completion: completion, notes: notes)
    }

    public func updatePost(postId: String, username: String, actionType: ActionType, completion:@escaping ((StatusOr<Response>, [String:Any]?) -> ()), notes: [String:Any]?) {
        var request: [String:Any] = [String:Any]()
        request["username"] = username
        request["post_id"] = postId
        request["action_type"] = ActionType.ToString(actionType: actionType)

        let path: String = constructIncompleteUrlPath() + ServerClient.UPDATE_POST_PATH
        executePost(targetUrl: path, jsonParams: request, completion: completion, notes: notes)
    }

    public func insertPost(username: String, postText: String, location: Location, completion:@escaping ((StatusOr<Response>, [String:Any]?) -> ()), notes: [String:Any]?) {
        var request: [String:Any] = [String:Any]()
        request["username"] = username
        request["post_text"] = postText
        request["location"] = location.toJson()

        let path: String = constructIncompleteUrlPath() + ServerClient.INSERT_POST_PATH;
        executePost(targetUrl: path, jsonParams: request, completion: completion, notes: notes);
    }

    public func insertComment(username: String, commentText: String, postId: String, completion:@escaping ((StatusOr<Response>, [String:Any]?) -> ()), notes: [String:Any]?) {
        var request: [String:Any] = [String:Any]()
        request["username"] = username
        request["comment_text"] = commentText
        request["post_id"] = postId

        let path: String = constructIncompleteUrlPath() + ServerClient.INSERT_COMMENT_PATH;
        executePost(targetUrl: path, jsonParams: request, completion: completion, notes: notes);
    }

    private func constructIncompleteUrlPath() -> String {
        return ServerClient.SERVER_DOMAIN + ServerClient.COMMON_PATH;
    }

    private func jsonObjectToUrlParameters(jsonRequest: [String:Any]) -> String {
        let urlParams: String = jsonObjectToUrlParametersImp(jsonObject: jsonRequest, prefix: "");
        return String(urlParams.dropLast())
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
    private func executeGet(targetUrl: String, jsonParams: [String:Any], completion: @escaping ((StatusOr<Response>), [String:Any]?) -> (), notes: [String:Any]?) {
        var url: String = targetUrl
        url += jsonObjectToUrlParameters(jsonRequest: jsonParams)

        url = url.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed)!
        //create the url with NSURL
        let urlObj = URL(string: url)!

        //create the session object
        let session = URLSession.shared

        //now create the URLRequest object using the url object
        let request = URLRequest(url: urlObj)

        //create dataTask using the session object to send data to the server
        let task = session.dataTask(with: request as URLRequest, completionHandler: { data, response, error in

            guard let data = data, error == nil else {
                completion(StatusOr<Response>(err: StatusError.GENERIC_CONNECTION_ERROR, msg: String(describing: error!)), notes)
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
                                                    + " -> \nurl: " + url), notes)
                }
                // Everything is okay at this point.
                completion(StatusOr<Response>(v: Response(jsonObject: json!)), notes)
            } catch let error {
                completion(StatusOr<Response>(err: StatusError.GENERIC_CONNECTION_ERROR, msg: String(describing: error) + "\nHTTP Code: " + String(httpStatusCode)
                    + " -> \nurl: " + url), notes)
            }
        })
        task.resume()
    }

    // Also, literally have no idea how this function really works.
    private func executePost(targetUrl: String, jsonParams: [String:Any], completion: @escaping ((StatusOr<Response>), [String:Any]?) -> (), notes: [String:Any]?) {
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
                completion(StatusOr<Response>(err: StatusError.GENERIC_CONNECTION_ERROR, msg: String(describing: error!)), notes)
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
                                                    + " -> \nurl: " + targetUrl), notes)
                }
                // Everything is okay at this point.
                completion(StatusOr<Response>(v: Response(jsonObject: json!)), notes)
            } catch let error {
                completion(StatusOr<Response>(err: StatusError.GENERIC_CONNECTION_ERROR, msg: String(describing: error) + "\nHTTP Code: " + String(httpStatusCode)
                    + " -> \nurl: " + targetUrl), notes)
            }
        }
        task.resume()
    }
}
