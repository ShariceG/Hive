//
//  CommentView.swift
//  Hive
//
//  Created by Chuck Onwuzuruike on 10/8/18.
//  Copyright © 2018 Chuck Onwuzuruike. All rights reserved.
//

import UIKit

protocol CommentViewDelegate: class {
    func performAction(commentView: CommentView, actionType: ActionType)
}

class CommentView: UITableViewCell {
    
    @IBOutlet weak var commentDislikeBn: UIButton!
    @IBOutlet weak var commentLikeBn: UIButton!
    @IBOutlet weak var userLabel: UILabel!
    @IBOutlet weak var commentTextView: UITextView!
    private(set) var comment: Comment? = nil

    private var _delegate: CommentViewDelegate?
    
    var delegate: CommentViewDelegate {
        set { _delegate = newValue }
        get { return _delegate! }
    }
    
    public func configure(comment: Comment) {
        userLabel.text = comment.username
        commentTextView.text = comment.commentText
        self.comment = comment
        commentDislikeBn.setTitle("Dislike: " + String(comment.dislikes), for: UIControl.State.normal)
        commentLikeBn.setTitle("Like: " + String(comment.likes), for: UIControl.State.normal)
        
        commentLikeBn.isEnabled = true;
        commentDislikeBn.isEnabled = true;
        // Handle like/dislike behavior.
        DispatchQueue.main.async {
            switch comment.userActionType {
            case ActionType.LIKE:
                self.commentLikeBn.setTitleColor(UIColor.orange, for: UIControl.State.normal)
                self.commentDislikeBn.setTitleColor(UIColor.blue, for: UIControl.State.normal)
                break;
            case ActionType.DISLIKE:
                self.commentDislikeBn.setTitleColor(UIColor.orange, for: UIControl.State.normal)
                self.commentLikeBn.setTitleColor(UIColor.blue, for: UIControl.State.normal)
                break;
            case ActionType.NO_ACTION:
                self.commentLikeBn.setTitleColor(UIColor.blue, for: UIControl.State.normal)
                self.commentDislikeBn.setTitleColor(UIColor.blue, for: UIControl.State.normal)
                break;
            }
        }
    }
    
    public func configure(comment: Comment, delegate: CommentViewDelegate) {
        configure(comment: comment)
        self.delegate = delegate
    }
    
    public func configureDisable(comment: Comment) {
        configure(comment: comment)
        self.isUserInteractionEnabled = false
    }
    
    public func disableLikeAndDislikeButtons() {
        commentDislikeBn.isEnabled = false
        commentLikeBn.isEnabled = false
    }
    
    @IBAction func commentDislikeBnAction(_ sender: UIButton) {
        disableLikeAndDislikeButtons()
        switch comment!.userActionType {
        case ActionType.NO_ACTION, ActionType.LIKE:
            delegate.performAction(commentView: self, actionType: ActionType.DISLIKE)
            break;
        case ActionType.DISLIKE:
            delegate.performAction(commentView: self, actionType: ActionType.NO_ACTION)
            break;
        }
    }
    
    @IBAction func commentLikeBnAction(_ sender: UIButton) {
        disableLikeAndDislikeButtons()
        switch comment!.userActionType {
        case ActionType.NO_ACTION, ActionType.DISLIKE:
            delegate.performAction(commentView: self, actionType: ActionType.LIKE)
            break;
        case ActionType.LIKE:
            delegate.performAction(commentView: self, actionType: ActionType.NO_ACTION)
            break;
        }
    }
    
}
