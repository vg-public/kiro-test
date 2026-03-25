interface ErrorMessageProps {
  message: string
  type?: 'warning' | 'error'
}

export default function ErrorMessage({ message, type = 'error' }: ErrorMessageProps) {
  return (
    <div className={`error-message ${type}`} role="alert">
      {message}
    </div>
  )
}
