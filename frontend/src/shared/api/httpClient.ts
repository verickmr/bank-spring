type ErrorResponse = {
  message?: string
}

export class HttpError extends Error {
  constructor(
    message: string,
    public readonly status: number,
  ) {
    super(message)
    this.name = 'HttpError'
  }
}

export async function request<T>(path: string, init: RequestInit = {}): Promise<T> {
  const headers = new Headers(init.headers)
  const token = tokenStorage.get()

  if (init.body && !headers.has('Content-Type')) {
    headers.set('Content-Type', 'application/json')
  }

  if (token && !headers.has('Authorization')) {
    headers.set('Authorization', `Bearer ${token}`)
  }

  let response: Response

  try {
    response = await fetch(path, { ...init, headers })
  } catch {
    throw new HttpError('Não foi possível conectar à API.', 0)
  }

  const body = response.status === 204
    ? null
    : await response.json().catch(() => null) as ErrorResponse | null

  if (!response.ok) {
    throw new HttpError(
      body?.message ?? 'Não foi possível concluir a solicitação.',
      response.status,
    )
  }

  return body as T
}
import { tokenStorage } from '../storage/tokenStorage'
