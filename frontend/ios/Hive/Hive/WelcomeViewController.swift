//
//  WelcomeViewController.swift
//  Hive
//
//  Created by Chuck Onwuzuruike on 4/5/20.
//  Copyright © 2020 Chuck Onwuzuruike. All rights reserved.
//

import UIKit

class WelcomeViewController : UIViewController, SignInPageFragment {
    public var signInDelegate: SignInDelegate?
    private var args: [String:Any]?
    
    override func viewDidLoad() {
        super.viewDidLoad()
    }
    
    func setSignInPageDelegate(delegate: SignInDelegate) {
        signInDelegate = delegate
    }
    
    func setArgs(args: [String:Any]) {
        self.args = args
    }

    @IBAction func beginBnAction(_ sender: UIButton) {
        signInDelegate?.goToMainApp()
    }
    
}
