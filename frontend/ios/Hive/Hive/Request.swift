//
//  Request.swift
//  Hive
//
//  Created by Chuck Onwuzuruike on 10/4/18.
//  Copyright © 2018 Chuck Onwuzuruike. All rights reserved.
//

import Foundation

class Request {
    
    struct RequestParams {
        var user: User?
        var post: Post?
        var comment: Comment?
        var actionType: ActionType?
    }
    
    private(set) var params: RequestParams
    private(set) var requestType: RequestType
    private(set) var retries: Int
    
    init(type: RequestType, params: RequestParams, retries: Int) {
        self.requestType = type
        self.params = params
        self.retries = retries
    }
    
    convenience init(type: RequestType, params: RequestParams) {
        self.init(type: type, params: params, retries: 0)
    }
    
}
