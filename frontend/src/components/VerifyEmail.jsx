import React, { useState } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { Mail, ArrowRight } from 'lucide-react';

const VerifyEmail = () => {
  const location = useLocation();
  const email = location.state?.email || 'your email';
  const [resending, setResending] = useState(false);
  const [resendMessage, setResendMessage] = useState('');

  const handleResend = async () => {
    setResending(true);
    setResendMessage('');

    try {
      const response = await fetch('http://localhost:8080/auth/resend-verification', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ email }),
      });

      if (response.ok) {
        setResendMessage('Verification email sent successfully!');
      } else {
        setResendMessage('Failed to resend email. Please try again.');
      }
    } catch (error) {
      setResendMessage('An error occurred. Please try again later.');
    } finally {
      setResending(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center p-6 bg-gradient-to-br from-background via-accent/20 to-background">
      <div className="w-full max-w-md">
        <div className="bg-card border border-border rounded-xl shadow-lg p-8">
          <div className="flex flex-col items-center text-center gap-6">
            {/* Icon */}
            <div className="w-20 h-20 rounded-full bg-primary/10 flex items-center justify-center">
              <Mail className="w-10 h-10 text-primary" />
            </div>

            {/* Heading */}
            <div className="space-y-2">
              <h1 className="text-3xl font-bold text-foreground">Check Your Email</h1>
              <p className="text-muted-foreground">
                We've sent a verification link to <strong className="text-foreground">{email}</strong>
              </p>
            </div>

            {/* Instructions */}
            <div className="w-full space-y-4 text-left bg-muted/30 rounded-lg p-4">
              <h3 className="font-semibold text-foreground">Next steps:</h3>
              <ol className="list-decimal list-inside space-y-2 text-sm text-muted-foreground">
                <li>Open the email in your inbox</li>
                <li>Click the verification link</li>
                <li>Come back and log in to your account</li>
              </ol>
            </div>

            {/* Resend Message */}
            {resendMessage && (
              <div className={`w-full p-4 rounded-lg ${
                resendMessage.includes('success') 
                  ? 'bg-green-50 dark:bg-green-950/20 border border-green-200 dark:border-green-800'
                  : 'bg-red-50 dark:bg-red-950/20 border border-red-200 dark:border-red-800'
              }`}>
                <p className={`text-sm ${
                  resendMessage.includes('success')
                    ? 'text-green-800 dark:text-green-200'
                    : 'text-red-800 dark:text-red-200'
                }`}>
                  {resendMessage}
                </p>
              </div>
            )}

            {/* Note */}
            <div className="w-full p-4 bg-blue-50 dark:bg-blue-950/20 border border-blue-200 dark:border-blue-800 rounded-lg">
              <p className="text-sm text-blue-800 dark:text-blue-200">
                <span className="font-semibold">Didn't receive an email?</span><br />
                Check your spam folder or request a new verification email.
              </p>
            </div>

            {/* Action Buttons */}
            <div className="w-full flex flex-col gap-3 mt-4">
              <Link
                to="/login"
                className="h-12 w-full bg-primary hover:bg-primary/90 text-primary-foreground font-semibold rounded-lg transition-colors flex items-center justify-center gap-2"
              >
                Go to Login
                <ArrowRight size={18} />
              </Link>
              
              <button
                onClick={handleResend}
                disabled={resending}
                className="h-12 w-full border border-border hover:bg-accent text-foreground font-medium rounded-lg transition-colors disabled:opacity-60 disabled:cursor-not-allowed"
              >
                {resending ? 'Sending...' : 'Resend Verification Email'}
              </button>
            </div>
          </div>
        </div>

        {/* Back to signup link */}
        <div className="text-center mt-6">
          <Link 
            to="/signup" 
            className="text-sm text-muted-foreground hover:text-foreground transition-colors"
          >
            ← Back to sign up
          </Link>
        </div>
      </div>
    </div>
  );
};

export default VerifyEmail;
