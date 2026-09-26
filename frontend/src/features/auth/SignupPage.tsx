import { Link } from 'react-router'
import { Button } from '@/components/ui/button'
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card'
import { CustomerForm } from '../customers/CustomerForm'
import { Brand } from '../../shared/components/Brand/Brand'

export function SignupPage() {
  return (
    <div className="grid min-h-svh grid-rows-[auto_1fr] bg-muted/40">
      <header className="border-b bg-background">
        <div className="mx-auto flex min-h-16 w-full max-w-6xl items-center px-4">
          <Brand />
        </div>
      </header>

      <main className="grid place-items-center px-4 py-10">
        <div className="w-full max-w-lg">
          <Button asChild variant="ghost" className="mb-4">
            <Link to="/login">← Voltar para o login</Link>
          </Button>

          <Card>
            <CardHeader>
              <CardTitle>Criar cadastro</CardTitle>
              <CardDescription>
                Cadastre seus dados para acessar o sistema.
              </CardDescription>
            </CardHeader>
            <CardContent>
              <CustomerForm
                redirectTo="/login"
                redirectState={{ registrationComplete: true }}
                submitLabel="Criar cadastro"
              />
            </CardContent>
          </Card>
        </div>
      </main>
    </div>
  )
}
