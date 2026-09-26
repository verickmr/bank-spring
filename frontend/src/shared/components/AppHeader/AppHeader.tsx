import { useQueryClient } from '@tanstack/react-query'
import { useNavigate } from 'react-router'
import { Button } from '@/components/ui/button'
import { tokenStorage } from '../../storage/tokenStorage'
import { Brand } from '../Brand/Brand'

export function AppHeader() {
  const navigate = useNavigate()
  const queryClient = useQueryClient()

  const logout = () => {
    tokenStorage.remove()
    queryClient.clear()
    navigate('/login', { replace: true })
  }

  return (
    <header className="border-b bg-background">
      <div className="mx-auto flex min-h-16 w-full max-w-6xl items-center justify-between px-4">
        <Brand />
        <Button variant="outline" onClick={logout}>
          Sair
        </Button>
      </div>
    </header>
  )
}
