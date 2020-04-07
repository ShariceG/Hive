//
//  EmailViewController.swift
//  Hive
//
//  Created by Chuck Onwuzuruike on 4/5/20.
//  Copyright © 2020 Chuck Onwuzuruike. All rights reserved.
//

import UIKit

class EnterPinCodeViewController : UIViewController, SignInPageFragment {
    
    @IBOutlet weak var pinCodeTextField: UITextField!
    private var signInDelegate: SignInDelegate?
    private let client: ServerClient = ServerClient()
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

    @IBAction func hiveInBnAction(_ sender: UIButton) {
        let pin = pinCodeTextField.text!
        if (pin.isEmpty) {
            showAlert(message: "Code cannot be empty")
            return
        }
        disableUserActivity()
        print("Pin: " + pin)
        let username = args!["username"] as! String
        let email = args!["email"] as! String
        client.checkVerificationCode(username: username, email: email, code: pin, completion: checkVerificationCode, notes: nil)
    }
    
    private func checkVerificationCode(responseOr: StatusOr<Response>, notes: [String:Any]?) {
        if (responseOr.hasError()) {
            showInternalServerErrorAlert()
            return
        }
        let response = responseOr.get()
        if (!response.ok()) {
            showAlert(title: "Oh...", message: response.serverMessage)
            return
        }
        let username = args!["username"] as! String
        let email = args!["email"] as! String
        signInDelegate!.saveLogInData(username: username, email: email, isSignUpVerified: true)
        DispatchQueue.main.async {
            self.signInDelegate?.goToMainAppOrWelcomeIfLogIn(args: self.args!)
        }
    }
}
