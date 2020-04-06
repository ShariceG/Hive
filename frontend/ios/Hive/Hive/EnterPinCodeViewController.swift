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
            return
        }
        print("Pin: " + pin)
        let username = args!["username"] as! String
        let email = args!["email"] as! String
        client.checkVerificationCode(username: username, email: email, code: pin, completion: checkVerificationCode, notes: nil)
    }
    
    private func checkVerificationCode(responseOr: StatusOr<Response>, notes: [String:Any]?) {
        let baseStr: String = "createNewUserCompletion => "
        if (responseOr.hasError()) {
            // Handle likley connection error
            print(baseStr + "Connection Failure: " + responseOr.getErrorMessage())
            return
        }
        let response = responseOr.get()
        if (!response.ok()) {
            // Handle server error
            print(baseStr + "ServerStatusCode: " + String(describing: response.serverStatusCode))
            return
        }
        DispatchQueue.main.async {
            self.signInDelegate?.goToMainAppOrWelcomeIfLogIn(args: self.args!)
        }
    }
}
