import { zodResolver } from '@hookform/resolvers/zod'
import { useForm } from 'react-hook-form'
import { Alert, AlertDescription } from '@/components/ui/alert'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { loginSchema, type LoginFormData } from './auth.schema'
import { useLogin } from './useLogin'

export function LoginForm() {
  const login = useLogin()
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<LoginFormData>({
    resolver: zodResolver(loginSchema),
    defaultValues: { cpf: '', senha: '' },
  })

  const submit = handleSubmit((credentials) => {
    login.reset()
    login.mutate(credentials)
  })

  return (
    <form className="grid gap-5" onSubmit={submit} noValidate>
      <div className="grid gap-2">
        <Label htmlFor="cpf">CPF</Label>
        <Input
          id="cpf"
          type="text"
          inputMode="numeric"
          autoComplete="username"
          maxLength={11}
          placeholder="Digite os 11 dígitos"
          {...register('cpf', {
            setValueAs: (value: string) => value.replace(/\D/g, ''),
          })}
          aria-invalid={Boolean(errors.cpf)}
          aria-describedby={errors.cpf ? 'cpf-error' : undefined}
          className="h-10"
        />
        {errors.cpf && (
          <p id="cpf-error" className="text-xs text-destructive">
            {errors.cpf.message}
          </p>
        )}
      </div>

      <div className="grid gap-2">
        <Label htmlFor="senha">Senha</Label>
        <Input
          id="senha"
          type="password"
          autoComplete="current-password"
          placeholder="Digite sua senha"
          {...register('senha')}
          aria-invalid={Boolean(errors.senha)}
          aria-describedby={errors.senha ? 'senha-error' : undefined}
          className="h-10"
        />
        {errors.senha && (
          <p id="senha-error" className="text-xs text-destructive">
            {errors.senha.message}
          </p>
        )}
      </div>

      {login.isError && (
        <Alert variant="destructive">
          <AlertDescription>{login.error.message}</AlertDescription>
        </Alert>
      )}

      {login.isSuccess && (
        <Alert className="border-emerald-200 bg-emerald-50 text-emerald-800">
          <AlertDescription className="text-emerald-800">
            Acesso autorizado.
          </AlertDescription>
        </Alert>
      )}

      <Button className="h-10 w-full" type="submit" disabled={login.isPending}>
        {login.isPending ? 'Entrando…' : 'Entrar'}
      </Button>
    </form>
  )
}
