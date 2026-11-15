# Contagem

App Android que coloca uma contagem regressiva no fundo da tela de bloqueio ("42 dias para as férias"). O número se atualiza sozinho logo depois da meia-noite. A tela inicial não muda.

- Kotlin + Jetpack Compose (Material 3)
- `minSdk 26` (Android 8) · `targetSdk 36` (Android 16, exigido pela Play Store)
- WorkManager para a atualização diária

## Rodar no seu computador

1. **Instale o Android Studio** (versão estável mais recente): https://developer.android.com/studio. Ele já traz o JDK; não precisa instalar Java.
2. **Abra o projeto**: *File → Open* e escolha a pasta do repositório clonado. Na primeira vez o Studio baixa o Gradle e as dependências, o que leva alguns minutos. Se pedir para instalar o **Android 16 (API 36)**, aceite. Ou instale em *Tools → SDK Manager*.
3. **Escolha onde rodar**:
   - **Celular de verdade (recomendado, porque o emulador nem sempre mostra a tela de bloqueio direito):**
     1. No celular, abra *Configurações → Sobre o telefone* e toque 7 vezes em *Número da versão*.
     2. Em *Opções do desenvolvedor*, ative a *Depuração USB*.
     3. Ligue o celular no USB e aceite o aviso "Permitir depuração".
     - Também dá para conectar sem cabo, em *Depuração por Wi-Fi* → *Device Manager → Pair devices using Wi-Fi*.
   - **Emulador:** *Device Manager → Create device* → escolha um Pixel com API 36.
4. Selecione o dispositivo na barra de cima e clique em **Run ▶**.

A versão de teste se instala como `com.victorprata.contagem.debug`. Por isso ela fica lado a lado com a versão da Play Store, sem conflito.

Pelo terminal, com o celular conectado: `./gradlew installDebug` (no Windows: `gradlew.bat installDebug`).

### Testar a atualização da meia-noite

Em *App Inspection → Background Task Inspector* você vê o agendamento `atualizacao-diaria` e quando ele vai rodar. Além disso, ao abrir o app, ele corrige o número se a atualização da noite tiver atrasado.

## Build na nuvem (GitHub Actions)

Cada push na `main` gera:

- **apk-teste**: APK para instalar direto no celular. Fica na aba *Actions*, dentro da execução, em *Artifacts*.
- **aab-play-store**: o arquivo para enviar ao Play Console. Só é gerado depois de configurar os secrets abaixo.

### Chave de upload da Play Store

1. No Android Studio, use *Build → Generate Signed App Bundle → Create new* e guarde o `.jks` e as senhas **fora do repositório**.
2. Converta o arquivo para texto no PowerShell:
   `[Convert]::ToBase64String([IO.File]::ReadAllBytes("upload.jks")) | Set-Clipboard`
3. Em *Settings → Secrets and variables → Actions*, crie os secrets `UPLOAD_KEYSTORE_BASE64`, `UPLOAD_KEYSTORE_PASSWORD`, `UPLOAD_KEY_ALIAS` e `UPLOAD_KEY_PASSWORD`.

O `versionCode` usa o número da execução do Actions, então ele sempre aumenta, como a Play exige.

> O `applicationId` (`com.victorprata.contagem`) não pode mudar depois do primeiro envio à Play Store.
