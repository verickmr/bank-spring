import { Navigate, Outlet, useLocation } from 'react-router'
import { tokenStorage } from '../storage/tokenStorage'

export function ProtectedRoute() {
  const location = useLocation()

  if (!tokenStorage.get()) {
    return <Navigate to="/login" replace state={{ from: location }} />
  }

  return <Outlet />
}
