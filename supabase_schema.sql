-- RuletAPP Database Schema
-- Ejecutar en Supabase SQL Editor

-- Tabla de opciones
CREATE TABLE IF NOT EXISTS opciones (
  id BIGSERIAL PRIMARY KEY,
  texto TEXT NOT NULL,
  created_at TIMESTAMPTZ DEFAULT NOW()
);

-- Tabla de resultados
CREATE TABLE IF NOT EXISTS resultados (
  id BIGSERIAL PRIMARY KEY,
  resultado TEXT NOT NULL,
  timestamp BIGINT NOT NULL,
  created_at TIMESTAMPTZ DEFAULT NOW()
);

-- Índices para mejor performance
CREATE INDEX IF NOT EXISTS idx_opciones_created ON opciones(created_at DESC);
CREATE INDEX IF NOT EXISTS idx_resultados_timestamp ON resultados(timestamp DESC);

-- Habilitar Row Level Security (RLS)
ALTER TABLE opciones ENABLE ROW LEVEL SECURITY;
ALTER TABLE resultados ENABLE ROW LEVEL SECURITY;

-- Políticas de acceso público (para desarrollo)
CREATE POLICY "Permitir lectura pública de opciones"
  ON opciones FOR SELECT
  USING (true);

CREATE POLICY "Permitir inserción pública de opciones"
  ON opciones FOR INSERT
  WITH CHECK (true);

CREATE POLICY "Permitir eliminación pública de opciones"
  ON opciones FOR DELETE
  USING (true);

CREATE POLICY "Permitir lectura pública de resultados"
  ON resultados FOR SELECT
  USING (true);

CREATE POLICY "Permitir inserción pública de resultados"
  ON resultados FOR INSERT
  WITH CHECK (true);

CREATE POLICY "Permitir eliminación pública de resultados"
  ON resultados FOR DELETE
  USING (true);
