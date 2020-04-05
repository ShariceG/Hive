//
//  SignInPageViewController.swift
//  Hive
//
//  Created by Chuck Onwuzuruike on 4/4/20.
//  Copyright © 2020 Chuck Onwuzuruike. All rights reserved.
//

import UIKit

protocol SignInDelegate: class {
    func goEnterEmailAddress()
    func goEnterEmailAddressAndUsername()
    func goEnterPinCode()
    func goWelcome()
    func goToMainAppOrWelcomeIfLogIn()
    func goToMainApp()
}

protocol SignInPageFragment: class {
    func setSignInPageDelegate(delegate: SignInDelegate)
}

class SignInPageViewController : UIPageViewController, SignInDelegate  {
    
    private let GO_TO_MAIN_APP_SEGUE: String = "GoToMainAppSegue"
    private let client: ServerClient = ServerClient()
    private var isSignUp: Bool = false

    override func viewDidLoad() {
        super.viewDidLoad()
        setCurrentController(storyboardId: "LogInSignUpViewController")
    }
    
    private func alreadyVerified() -> Bool {
        return UserDefaults.standard.string(forKey: "username") != nil
    }
    
    private func setCurrentController(storyboardId: String) {
        let controller = newViewController(storyboardName: "SignIn", storyboardId: storyboardId)
        controller.hideKeyboardWhenTapped()
        (controller as! SignInPageFragment).setSignInPageDelegate(delegate: self)
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
    
    func goEnterEmailAddress() {
        setCurrentController(storyboardId: "EmailViewController")
    }
    
    func goEnterEmailAddressAndUsername() {
        isSignUp = true
        setCurrentController(storyboardId: "EmailAndUsernameViewController")
    }
    
    func goEnterPinCode() {
        setCurrentController(storyboardId: "EnterPinViewController")
    }
    
    func goWelcome() {
        setCurrentController(storyboardId: "WelcomeViewController")
    }
    
    func goToMainAppOrWelcomeIfLogIn() {
        if isSignUp {
            goWelcome()
        } else {
            goToMainApp()
        }
    }
    
    func goToMainApp() {
        self.performSegue(withIdentifier: GO_TO_MAIN_APP_SEGUE, sender: self)
    }
    
}
