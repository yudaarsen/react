import '../../styles/Modal.css';

export default function Modal( { children, isOpen, onClose } ) {
    if(!isOpen) return null;
    
    return (
        <div>
            <div className='overlay' onClick={onClose} />
            <div className="modal">
                { children }
            </div>
        </div>
    );
} 