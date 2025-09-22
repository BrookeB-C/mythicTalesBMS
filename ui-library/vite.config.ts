import { defineConfig } from 'vite';
import { resolve } from 'path';

export default defineConfig({
  build: {
    lib: {
      entry: resolve(__dirname, 'src/index.ts'),
      name: 'MythicTalesUI',
      formats: ['es', 'umd'],
      fileName: (format) => `mt-ui.${format}.js`
    },
    outDir: resolve(__dirname, '../src/main/resources/static/ui'),
    emptyOutDir: true,
    sourcemap: true,
    rollupOptions: {
      output: {
        assetFileNames: (assetInfo) => {
          if (assetInfo.name && assetInfo.name.endsWith('.css')) {
            return 'styles/[name][extname]';
          }
          return 'assets/[name]-[hash][extname]';
        }
      }
    }
  }
});
