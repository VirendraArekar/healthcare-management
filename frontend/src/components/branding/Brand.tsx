import mark from "../../images/brand-mark.svg";
export default function Brand() {
  return (
    <div className="brand">
      <img src={mark} width="38" height="38" alt="" />
      <div>
        <strong>Harbor Health</strong>
        <small>CONNECTED CARE PLATFORM</small>
      </div>
    </div>
  );
}
