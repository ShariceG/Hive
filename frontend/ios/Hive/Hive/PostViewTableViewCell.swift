//
//  PostViewTableViewCell.swift
//  Hive
//
//  Created by Chuck Onwuzuruike on 10/7/18.
//  Copyright © 2018 Chuck Onwuzuruike. All rights reserved.
//

import UIKit

class PostViewTableViewCell: UITableViewCell {
    
    @IBOutlet weak var userLabel: UILabel!
    @IBOutlet weak var postTextView: UITextView!
    @IBOutlet weak var dislikeBn: UIButton!
    @IBOutlet weak var commentBn: UIButton!
    @IBOutlet weak var likeBn: UIButton!
    
    var feedViewController: ViewController? = nil
    var post: Post? = nil
    
    public func configure(post: Post, feedViewController: ViewController) {
        userLabel.text = post.username
        postTextView.text = post.postText
        dislikeBn.setTitle("Dislike: " + String(post.dislikes), for: UIControlState.normal)
        likeBn.setTitle("Like: " + String(post.likes), for: UIControlState.normal)
        self.feedViewController = feedViewController
        self.post = post
    }
    
    @IBAction func commentBnAction(_ sender: UIButton) {
        feedViewController?.performSegue(withIdentifier: "seeCommentsSegue", sender: self)
    }
}
