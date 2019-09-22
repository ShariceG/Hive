//
//  CommentView.swift
//  Hive
//
//  Created by Chuck Onwuzuruike on 10/8/18.
//  Copyright © 2018 Chuck Onwuzuruike. All rights reserved.
//

import UIKit

protocol CommentViewDelegate: class {
    // Nothing to delegate
}

class CommentView: UITableViewCell {
    
    @IBOutlet weak var userLabel: UILabel!
    @IBOutlet weak var commentTextView: UITextView!
    private(set) var comment: Comment? = nil

    private var _delegate: CommentViewDelegate?
    
    var delegate: CommentViewDelegate {
        set { _delegate = newValue }
        get { return _delegate! }
    }
    
    public func configure(comment: Comment, delegate: CommentViewDelegate) {
        self.delegate = delegate
        userLabel.text = comment.username
        commentTextView.text = comment.commentText
        self.comment = comment
        self.isUserInteractionEnabled = false
    }
    
}
