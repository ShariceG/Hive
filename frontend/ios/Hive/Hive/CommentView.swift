//
//  CommentView.swift
//  Hive
//
//  Created by Chuck Onwuzuruike on 10/8/18.
//  Copyright © 2018 Chuck Onwuzuruike. All rights reserved.
//

import UIKit

class CommentView: UITableViewCell {
    
    @IBOutlet weak var userLabel: UILabel!
    @IBOutlet weak var commentTextView: UITextView!
    private(set) var comment: Comment? = nil
    
    public func configure(comment: Comment) {
        userLabel.text = comment.username
        commentTextView.text = comment.commentText
        self.comment = comment
        self.isUserInteractionEnabled = false
    }
    
}
