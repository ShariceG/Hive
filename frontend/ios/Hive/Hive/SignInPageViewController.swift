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
    func saveLogInData(username: String, email: String, isSignUpVerified: Bool)
}

protocol SignInPageFragment: class {
    func setSignInPageDelegate(delegate: SignInDelegate)
    func setArgs(args: [String:Any])
}

class SignInPageViewController : UIPageViewController, SignInDelegate  {
    
    private let GO_TO_MAIN_APP_SEGUE: String = "GoToMainAppSegue"
    private var isSignUp: Bool = false

    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        // If the user is already saved locally, we can skip signing in and go
        // directly to the app.
        let userObj = UserDefaults.standard.object(forKey: "user")
        if userObj != nil {
            let userData = userObj as! Data
            let decoder = JSONDecoder()
            if let user = try? decoder.decode(User.self, from: userData) {
                if user.isSignUpVerified {
                    goToMainApp()
                } else {
                    goEnterEmailAddress(args: [String:Any]())
                }
            }
        } else {
            goLogInOrSignUp()
        }
    }
    
    func getUserFromDefaults() -> User? {
        let userObj = UserDefaults.standard.object(forKey: "user")
        if userObj != nil {
            let userData = userObj as! Data
            let decoder = JSONDecoder()
            if let user = try? decoder.decode(User.self, from: userData) {
                return user
            } else {
                print("Error: Unable to get user from defaults but got obj: " + userObj.debugDescription)
            }
        } else {
            print("Error: No user in defaults.")
        }
        return nil
    }
    
    func saveLogInData(username: String, email: String, isSignUpVerified: Bool) {
        let user = User(username: username, email: email, isSignUpVerified: isSignUpVerified)
        let encoder = JSONEncoder()
        if let encoded = try? encoder.encode(user) {
            UserDefaults.standard.set(encoded, forKey: "user")
        } else {
            showAlert(message: "Unable to save locally")
        }
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
        setupUserInEnvironment()
        self.performSegue(withIdentifier: GO_TO_MAIN_APP_SEGUE, sender: self)
    }
    
    func setupUserInEnvironment() {
        print("Creating global environment with user...")
        Environment.createGlobalEnvironment()
        Global.environment?.setUser(user: getUserFromDefaults()!)
        print("Done.")
    }
    
}
