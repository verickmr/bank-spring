import { Link } from 'react-router'
import { Button } from '@/components/ui/button'
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card'
import { AppHeader } from '../../shared/components/AppHeader/AppHeader'
import { AccountForm } from './AccountForm'

export function AccountCreatePage() {
  return (
    <div className="min-h-svh bg-muted/40">
      <AppHeader />

      <main className="mx-auto w-full max-w-2xl px-4 py-8">
        <Button asChild variant="ghost" className="mb-4">
          <Link to="/">← Voltar para contas</Link>
        </Button>

        <Card>
          <CardHeader>
            <CardTitle>Abrir conta</CardTitle>
            <CardDescription>
              Informe os dados da nova conta vinculada ao seu cadastro.
            </CardDescription>
          </CardHeader>
          <CardContent>
            <AccountForm />
          </CardContent>
        </Card>
      </main>
    </div>
  )
}
