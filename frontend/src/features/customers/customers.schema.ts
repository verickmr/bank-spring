import { z } from 'zod'

export const customerCreateSchema = z.object({
  cpf: z
    .string()
    .trim()
    .regex(/^\d{11}$/, 'Informe um CPF com 11 dígitos.'),
  nome: z
    .string()
    .trim()
    .min(3, 'O nome deve ter pelo menos 3 caracteres.')
    .max(100, 'O nome deve ter no máximo 100 caracteres.'),
  email: z
    .string()
    .trim()
    .email('Informe um e-mail válido.'),
  senha: z
    .string()
    .min(8, 'A senha deve ter pelo menos 8 caracteres.')
    .max(72, 'A senha deve ter no máximo 72 caracteres.'),
})

export type CustomerCreateFormData = z.infer<typeof customerCreateSchema>
