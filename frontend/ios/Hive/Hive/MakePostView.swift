//
//  MakePostView.swift
//  Hive
//
//  Created by Chuck Onwuzuruike on 1/3/20.
//  Copyright © 2020 Chuck Onwuzuruike. All rights reserved.
//

import UIKit

protocol MakePostViewDelegate: class {
    func makePost(text: String)
}

class MakePostView: UIView {

    @IBOutlet weak var postTextView: UITextView!
    @IBOutlet weak var cancelButton: UIButton!
    @IBOutlet weak var postButton: UIButton!
    
    private(set) var delegate: MakePostViewDelegate?
    private(set) var parent: UIView?
    
    public func configure(parent: UIView, delegate: MakePostViewDelegate) {
        self.backgroundColor = UIColor.black.withAlphaComponent(0.5)
        self.parent = parent
        self.delegate = delegate
    }
    
    public func show() {
        self.isUserInteractionEnabled = true
        self.parent?.addSubview(self)
        self.parent?.bringSubviewToFront(self)
    }
    
    public func clearAndShow() {
        clearPostText()
        show()
    }
    
    public func hide() {
        removeFromSuperview()
    }
    
    public func clearPostText() {
        DispatchQueue.main.async {
            self.postTextView.text = ""
        }
    }
    
    @IBAction func cancelButtonAction(_ sender: UIButton) {
        hide()
    }
    
    @IBAction func postButtonAction(_ sender: UIButton) {
        let text = postTextView.text.trimmingCharacters(in: .whitespaces)
        if text.isEmpty {
            return
        }
        delegate!.makePost(text: text)
    }
    
}
