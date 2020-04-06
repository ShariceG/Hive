//
//  SignInPageViewController.swift
//  Hive
//
//  Created by Chuck Onwuzuruike on 4/4/20.
//  Copyright © 2020 Chuck Onwuzuruike. All rights reserved.
//

import UIKit

protocol SignInDelegate: class {
    func goLogInOrSignUp()
    func goEnterEmailAddress(args: [String:Any])
    func goEnterEmailAddressAndUsername(args: [String:Any])
    func goEnterPinCode(args: [String:Any])
    func goWelcome(args: [String:Any])
    func goToMainAppOrWelcomeIfLogIn(args: [String:Any])
    func goToMainApp()
}

protocol SignInPageFragment: class {
    func setSignInPageDelegate(delegate: SignInDelegate)
    func setArgs(args: [String:Any])
}

class SignInPageViewController : UIPageViewController, SignInDelegate  {
    
    private let GO_TO_MAIN_APP_SEGUE: String = "GoToMainAppSegue"
    private var isSignUp: Bool = false

    override func viewDidLoad() {
        super.viewDidLoad()
        goLogInOrSignUp()
    }
    
    private func alreadyVerified() -> Bool {
        return UserDefaults.standard.string(forKey: "username") != nil
    }
    
    private func setCurrentController(storyboardId: String, args: [String:Any]) {
        let controller = newViewController(storyboardName: "SignIn", storyboardId: storyboardId)
        controller.hideKeyboardWhenTapped()
        (controller as! SignInPageFragment).setSignInPageDelegate(delegate: self)
        (controller as! SignInPageFragment).setArgs(args: args)
        setViewControllers([controller], direction: .forward, animated: false, completion: nil)
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if (segue.identifier == GO_TO_MAIN_APP_SEGUE) {
            // Dismiss is used if current view was presented via "show" and pop is used
            // if current view was presented any other way.
            // Since this is the first view controller, we have no idea how ios
            // presented it so we do both.
            self.dismiss(animated: true, completion: nil)
            self.navigationController?.popViewController(animated: true)
        }
    }
    
    func goLogInOrSignUp() {
        isSignUp = false
        setCurrentController(storyboardId: "LogInSignUpViewController", args: [String:Any]())
    }
    
    func goEnterEmailAddress(args: [String:Any]) {
        setCurrentController(storyboardId: "EmailViewController", args: args)
    }
    
    func goEnterEmailAddressAndUsername(args: [String:Any]) {
        isSignUp = true
        setCurrentController(storyboardId: "EmailAndUsernameViewController", args: args)
    }
    
    func goEnterPinCode(args: [String:Any]) {
        setCurrentController(storyboardId: "EnterPinViewController", args: args)
    }
    
    func goWelcome(args: [String:Any]) {
        setCurrentController(storyboardId: "WelcomeViewController", args: args)
    }
    
    func goToMainAppOrWelcomeIfLogIn(args: [String:Any]) {
        if isSignUp {
            goWelcome(args: args)
        } else {
            goToMainApp()
        }
    }
    
    func goToMainApp() {
        self.performSegue(withIdentifier: GO_TO_MAIN_APP_SEGUE, sender: self)
    }
    
}
