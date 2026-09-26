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
import { CustomerForm } from './CustomerForm'

export function CustomerCreatePage() {
  return (
    <div className="min-h-svh bg-muted/40">
      <AppHeader />

      <main className="mx-auto w-full max-w-2xl px-4 py-8">
        <Button asChild variant="ghost" className="mb-4">
          <Link to="/correntistas">← Voltar para correntistas</Link>
        </Button>

        <Card>
          <CardHeader>
            <CardTitle>Novo correntista</CardTitle>
            <CardDescription>
              Informe os dados pessoais e defina a senha inicial de acesso.
            </CardDescription>
          </CardHeader>
          <CardContent>
            <CustomerForm />
          </CardContent>
        </Card>
      </main>
    </div>
  )
}
