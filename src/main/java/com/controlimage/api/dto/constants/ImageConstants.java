package com.controlimage.api.dto.constants;

public final class ImageConstants {
    private ImageConstants() {
    }

    // ==========================
    // 📘 Swagger
    // ==========================
    public static final String TITLE = "Images";
    public static final String DESCRIPTION = "Gerencia o upload, download e ativação de imagens.";

    // ==========================
    // 🧱 Entity / API
    // ==========================
    public static final String SCHEMA = "control_images";
    public static final String BASE_API = "/api";
    public static final String BASE_PATH = BASE_API + "/image";

    // ==========================
    // 🔐 Roles
    // ==========================
    public static final String ADMIN_AUTHORITY = "hasAuthority('ADMIN')";

    // ==========================
    // 💬 Response Descriptions
    // ==========================
    public static final String UPLOAD = "Upload de imagem realizado com sucesso.";
    public static final String DOWNLOAD = "Download de imagem realizado com sucesso.";
    public static final String ACTIVE_IMAGE = "Imagem ativada com sucesso.";
    public static final String DELETED = "Imagem removida com sucesso.";
    public static final String NOT_FOUND = "Imagem não encontrada.";
    public static final String BAD_REQUEST = "Arquivo inválido ou parâmetros incorretos.";
    public static final String INTERNAL_ERROR = "Erro interno ao processar a solicitação.";

    // ==========================
    // 🧾 Parameter Descriptions
    // ==========================
    public static final String PARAM_IMAGE_ID = "Identificador único da imagem.";
    public static final String PARAM_FILE = "Arquivo da imagem a ser enviada.";
    public static final String PARAM_ACTIVE = "Define se a imagem deve ser ativada imediatamente.";
}
