const qrText = document.getElementById("qr-text");
const qrCodeDiv = document.getElementById("qr-code");
const generateBtn = document.getElementById("generate-btn");
const downloadBtn = document.getElementById("download-btn");

generateBtn.addEventListener('click', () => {
    qrCodeDiv.innerHTML= "";
    const qrCode = new qrCode(qrCodeDiv, {
        text: qrText.ariaValueMax,
        width: 256,
        height:256,
        colorDark:"#000000",
        colorLight: "#FFFFFF",
        correctLevel: qrCode.CorrectLevel.H,
    })
    downloadBtn.disabled = false;

    downloadBtn.addEventListener('click', () => {
        const canvas = qrCode._el.firstChild;
        const downloadLink = document.createElement("a");
        downloadLink.download = "qrcode.png";
        downloadLink.href = canvas.toDateURL("image/png");

        document.body.appendChild(downloadLink);
        downloadLink.click();
        document.body.removeChild(downloadLink);
    });
})