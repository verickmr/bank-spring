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

          <CardContent>
            <LoginForm />
          </CardContent>

          <CardFooter className="border-t pt-5">
            <p className="w-full text-center text-xs leading-5 text-muted-foreground">
            Seus dados são utilizados somente para autenticar o acesso.
            </p>
          </CardFooter>
        </Card>
      </main>

      <footer className="px-4 py-5 text-center text-xs text-muted-foreground">
        Ambiente seguro · Conta Segura
      </footer>
    </div>
  )
}
