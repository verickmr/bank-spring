import { useQueryClient } from '@tanstack/react-query'
import { Link, NavLink, useNavigate } from 'react-router'
import { Button } from '@/components/ui/button'
import { cn } from '@/lib/utils'
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
      <div className="mx-auto flex min-h-16 w-full max-w-6xl items-center gap-4 px-4">
        <Link to="/" aria-label="Ir para a visão geral">
          <Brand />
        </Link>

        <nav className="ml-auto flex items-center gap-1" aria-label="Navegação principal">
          <NavLink
            to="/"
            end
            className={({ isActive }) => cn(
              'rounded-md px-3 py-2 text-sm font-medium text-muted-foreground hover:text-foreground',
              isActive && 'bg-muted text-foreground',
            )}
          >
            Contas
          </NavLink>
          <NavLink
            to="/correntistas"
            className={({ isActive }) => cn(
              'rounded-md px-3 py-2 text-sm font-medium text-muted-foreground hover:text-foreground',
              isActive && 'bg-muted text-foreground',
            )}
          >
            Correntistas
          </NavLink>
        </nav>

        <Button variant="outline" onClick={logout}>
          Sair
        </Button>
      </div>
    </header>
  )
}
