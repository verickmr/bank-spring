import { Link, useLocation } from 'react-router'
import { Alert, AlertDescription } from '@/components/ui/alert'
import { Button } from '@/components/ui/button'
import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from '@/components/ui/card'
import { Brand } from '../../shared/components/Brand/Brand'
import { LoginForm } from './LoginForm'

export function LoginPage() {
  const location = useLocation()
  const registrationComplete = Boolean(
    (location.state as { registrationComplete?: boolean } | null)?.registrationComplete,
  )

  return (
    <div className="grid min-h-svh grid-rows-[auto_1fr_auto] bg-muted/40">
      <header className="border-b bg-background">
        <div className="mx-auto flex min-h-16 w-full max-w-6xl items-center justify-between px-4">
          <Brand />
          <span className="hidden text-xs text-muted-foreground sm:inline">
            Internet Banking
          </span>
        </div>
      </header>

      <main className="grid place-items-center px-4 py-10">
        <Card className="w-full max-w-md shadow-sm" aria-labelledby="login-title">
          <CardHeader>
            <CardTitle id="login-title" className="text-xl">
              Acessar conta
            </CardTitle>
            <CardDescription>Informe seus dados para continuar.</CardDescription>
          </CardHeader>

          <CardContent className="space-y-5">
            {registrationComplete && (
              <Alert className="border-emerald-200 bg-emerald-50 text-emerald-800">
                <AlertDescription className="text-emerald-800">
                  Cadastro concluído. Entre com seu CPF e sua senha.
                </AlertDescription>
              </Alert>
            )}
            <LoginForm />
          </CardContent>

          <CardFooter className="flex-col gap-3 border-t pt-5">
            <p className="text-center text-xs leading-5 text-muted-foreground">
              Ainda não possui acesso?
            </p>
            <Button asChild variant="outline" className="w-full">
              <Link to="/cadastro">Criar cadastro</Link>
            </Button>
          </CardFooter>
        </Card>
      </main>

      <footer className="px-4 py-5 text-center text-xs text-muted-foreground">
        Ambiente seguro · Conta Segura
      </footer>
    </div>
  )
}
