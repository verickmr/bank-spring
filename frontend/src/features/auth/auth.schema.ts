import { z } from 'zod'

export const loginSchema = z.object({
  cpf: z
    .string()
    .trim()
    .regex(/^\d{11}$/, 'Informe um CPF com 11 dígitos.'),
  senha: z.string().min(1, 'Informe sua senha.'),
})

export type LoginFormData = z.infer<typeof loginSchema>
