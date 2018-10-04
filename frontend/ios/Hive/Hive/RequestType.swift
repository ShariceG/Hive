//
//  RequestType.swift
//  Hive
//
//  Created by Chuck Onwuzuruike on 10/4/18.
//  Copyright © 2018 Chuck Onwuzuruike. All rights reserved.
//

import Foundation

enum RequestType: Int {
    case CREATE_USER
    case INSERT_COMMENT
    case INSERT_POST
    case GET_ALL_POST_COMMENTS
    case GET_ALL_POSTS_AROUND_USER
    case GET_ALL_POSTS_BY_USER
    case GET_ALL_POSTS_COMMENTED_ON_BY_USER
    case UPDATE_POST
}
