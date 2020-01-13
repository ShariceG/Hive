//
//  PostViewTableViewCell.swift
//  Hive
//
//  Created by Chuck Onwuzuruike on 10/7/18.
//  Copyright © 2018 Chuck Onwuzuruike. All rights reserved.
//

import UIKit

protocol PostViewDelegate: class {
    func commentButtonClick(postView: PostView)
    func performAction(postView: PostView, actionType: ActionType)
}

class PostView: UITableViewCell {
    
    @IBOutlet weak var userLabel: UILabel!
    @IBOutlet weak var postTextView: UITextView!
    @IBOutlet weak var dislikeBn: UIButton!
    @IBOutlet weak var commentBn: UIButton!
    @IBOutlet weak var likeBn: UIButton!
    @IBOutlet weak var dateLabel: UILabel!
    
    private var _delegate: PostViewDelegate?
    private var _post: Post? = nil
    
    var delegate: PostViewDelegate {
        set { _delegate = newValue }
        get { return _delegate! }
    }

    var post: Post {
        set { _post = newValue }
        get { return _post! }
    }
    
    public func configure(post: Post) {
        userLabel.text = post.username
        postTextView.text = post.postText
        dislikeBn.setTitle("Dislike: " + String(post.dislikes), for: UIControl.State.normal)
        likeBn.setTitle("Like: " + String(post.likes), for: UIControl.State.normal)
        dateLabel.text = self.timestampToDate(timestampSec: post.creationTimestampSec)
        self.post = post
        
        likeBn.isEnabled = true;
        dislikeBn.isEnabled = true;
        // Handle like/dislike behavior.
        DispatchQueue.main.async {
            switch post.userActionType {
            case ActionType.LIKE:
                self.likeBn.setTitleColor(UIColor.orange, for: UIControl.State.normal)
                self.dislikeBn.setTitleColor(UIColor.blue, for: UIControl.State.normal)
                break;
            case ActionType.DISLIKE:
                self.dislikeBn.setTitleColor(UIColor.orange, for: UIControl.State.normal)
                self.likeBn.setTitleColor(UIColor.blue, for: UIControl.State.normal)
                break;
            case ActionType.NO_ACTION:
                self.likeBn.setTitleColor(UIColor.blue, for: UIControl.State.normal)
                self.dislikeBn.setTitleColor(UIColor.blue, for: UIControl.State.normal)
                break;
            }
        }
    }
    
    public func configure(post: Post, delegate: PostViewDelegate) {
        configure(post: post)
        self.delegate = delegate
    }
    
    public func configureDisable(post: Post) {
        configure(post: post)
        self.isUserInteractionEnabled = false
    }
    
    public func disableLikeAndDislikeButtons() {
        dislikeBn.isEnabled = false
        likeBn.isEnabled = false
    }
    
    @IBAction func dislikeBnAction(_ sender: UIButton) {
        disableLikeAndDislikeButtons()
        switch post.userActionType {
        case ActionType.NO_ACTION, ActionType.LIKE:
            delegate.performAction(postView: self, actionType: ActionType.DISLIKE)
            break;
        case ActionType.DISLIKE:
            delegate.performAction(postView: self, actionType: ActionType.NO_ACTION)
            break;
        }
    }
    @IBAction func likeBnAction(_ sender: UIButton) {
        disableLikeAndDislikeButtons()
        switch post.userActionType {
        case ActionType.NO_ACTION, ActionType.DISLIKE:
            delegate.performAction(postView: self, actionType: ActionType.LIKE)
            break;
        case ActionType.LIKE:
            delegate.performAction(postView: self, actionType: ActionType.NO_ACTION)
            break;
        }
    }
    @IBAction func commentBnAction(_ sender: UIButton) {
        delegate.commentButtonClick(postView: self)
    }
}
